package com.obscura.game.data

import com.google.firebase.database.*
import com.obscura.game.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val partiesRef = database.getReference("parties")
    private val playersRef = database.getReference("players")
    private val storeRef = database.getReference("store")
    private val matchmakingRef = database.getReference("matchmaking")
    
    suspend fun createPlayer(name: String): Player {
        val playerId = playersRef.push().key ?: throw Exception("Failed to generate player ID")
        val player = Player(
            id = playerId,
            name = name,
            avatarId = "default",
            accessoryIds = emptyList(),
            coins = 100 // Starting coins
        )
        playersRef.child(playerId).setValue(player).await()
        return player
    }
    
    suspend fun getPlayer(playerId: String): Player? {
        val snapshot = playersRef.child(playerId).get().await()
        return snapshot.getValue(Player::class.java)
    }
    
    suspend fun updatePlayerCoins(playerId: String, coins: Int) {
        playersRef.child(playerId).child("coins").setValue(coins).await()
    }
    
    suspend fun updatePlayerAvatar(playerId: String, avatarId: String) {
        playersRef.child(playerId).child("avatarId").setValue(avatarId).await()
    }
    
    suspend fun addPlayerAccessory(playerId: String, accessoryId: String) {
        val player = getPlayer(playerId) ?: return
        val updatedAccessories = player.accessoryIds + accessoryId
        playersRef.child(playerId).child("accessoryIds").setValue(updatedAccessories).await()
    }
    
    suspend fun createParty(hostPlayer: Player): String {
        val partyCode = generatePartyCode()
        val party = GameParty(
            partyCode = partyCode,
            hostId = hostPlayer.id,
            players = mapOf(hostPlayer.id to hostPlayer.copy(isHost = true))
        )
        partiesRef.child(partyCode).setValue(party).await()
        return partyCode
    }
    
    suspend fun joinParty(partyCode: String, player: Player): Boolean {
        val snapshot = partiesRef.child(partyCode).get().await()
        if (!snapshot.exists()) return false
        
        val party = snapshot.getValue(GameParty::class.java) ?: return false
        if (party.gameState != GameState.WAITING) return false
        if (party.players.size >= 8) return false // Max 8 players
        
        partiesRef.child(partyCode).child("players").child(player.id).setValue(player).await()
        return true
    }
    
    fun observeParty(partyCode: String): Flow<GameParty?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val party = snapshot.getValue(GameParty::class.java)
                trySend(party)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        partiesRef.child(partyCode).addValueEventListener(listener)
        
        awaitClose {
            partiesRef.child(partyCode).removeEventListener(listener)
        }
    }
    
    suspend fun startGame(partyCode: String) {
        val snapshot = partiesRef.child(partyCode).get().await()
        val party = snapshot.getValue(GameParty::class.java) ?: return
        
        if (party.players.size < 3) return // Need at least 3 players
        
        val (word, category) = GameWords.getRandomWordAndCategory()
        val playerIds = party.players.keys.toList()
        val imposterId = playerIds.random()
        
        val updates = mapOf(
            "gameState" to GameState.HINT_ROUND.name,
            "word" to word,
            "category" to category,
            "imposterId" to imposterId,
            "currentRound" to 1
        )
        
        partiesRef.child(partyCode).updateChildren(updates).await()
    }
    
    suspend fun submitHint(partyCode: String, playerId: String, hint: String) {
        val hintsPath = "hints/$playerId"
        val snapshot = partiesRef.child(partyCode).child(hintsPath).get().await()
        val currentHints = snapshot.children.mapNotNull { it.getValue(String::class.java) }
        val updatedHints = currentHints + hint
        
        partiesRef.child(partyCode).child(hintsPath).setValue(updatedHints).await()
    }
    
    suspend fun submitVote(partyCode: String, voterId: String, votedPlayerId: String) {
        partiesRef.child(partyCode).child("votes").child(voterId).setValue(votedPlayerId).await()
        
        // Check if all players have voted
        val snapshot = partiesRef.child(partyCode).get().await()
        val party = snapshot.getValue(GameParty::class.java) ?: return
        
        if (party.votes.size == party.players.size) {
            // Process votes
            val voteCount = party.votes.values.groupingBy { it }.eachCount()
            val mostVoted = voteCount.maxByOrNull { it.value }?.key
            
            if (mostVoted == party.imposterId) {
                // Innocents win
                partiesRef.child(partyCode).child("gameState").setValue(GameState.GAME_OVER.name).await()
                // Award coins to innocents
                party.players.keys.forEach { playerId ->
                    if (playerId != party.imposterId) {
                        val player = party.players[playerId]
                        if (player != null) {
                            updatePlayerCoins(playerId, player.coins + 50)
                        }
                    }
                }
            } else {
                // Move to next round or imposter wins
                if (party.currentRound >= party.maxRounds) {
                    // Imposter wins
                    partiesRef.child(partyCode).child("gameState").setValue(GameState.GAME_OVER.name).await()
                    val imposter = party.players[party.imposterId]
                    if (imposter != null) {
                        updatePlayerCoins(party.imposterId, imposter.coins + 100)
                    }
                } else {
                    // Next round
                    val updates = mapOf(
                        "currentRound" to party.currentRound + 1,
                        "votes" to emptyMap<String, String>()
                    )
                    partiesRef.child(partyCode).updateChildren(updates).await()
                }
            }
        }
    }
    
    suspend fun moveToVoting(partyCode: String) {
        partiesRef.child(partyCode).child("gameState").setValue(GameState.VOTING.name).await()
    }
    
    suspend fun joinMatchmaking(player: Player): String? {
        // Look for an open party
        val snapshot = matchmakingRef.get().await()
        for (child in snapshot.children) {
            val partyCode = child.getValue(String::class.java)
            if (partyCode != null) {
                val partySnapshot = partiesRef.child(partyCode).get().await()
                val party = partySnapshot.getValue(GameParty::class.java)
                if (party != null && party.gameState == GameState.WAITING && party.players.size < 8) {
                    if (joinParty(partyCode, player)) {
                        matchmakingRef.child(child.key!!).removeValue().await()
                        return partyCode
                    }
                }
            }
        }
        
        // No open party found, create one
        val partyCode = createParty(player)
        matchmakingRef.push().setValue(partyCode).await()
        return partyCode
    }
    
    suspend fun getStoreItems(): List<StoreItem> {
        val snapshot = storeRef.get().await()
        return snapshot.children.mapNotNull { it.getValue(StoreItem::class.java) }
    }
    
    suspend fun initializeStore() {
        val items = listOf(
            StoreItem("avatar_ninja", "Ninja", ItemType.AVATAR, 100),
            StoreItem("avatar_spy", "Spy", ItemType.AVATAR, 150),
            StoreItem("avatar_detective", "Detective", ItemType.AVATAR, 200),
            StoreItem("avatar_thief", "Thief", ItemType.AVATAR, 150),
            StoreItem("accessory_hat", "Hat", ItemType.ACCESSORY, 50),
            StoreItem("accessory_glasses", "Sunglasses", ItemType.ACCESSORY, 75),
            StoreItem("accessory_mask", "Mask", ItemType.ACCESSORY, 100),
            StoreItem("accessory_cape", "Cape", ItemType.ACCESSORY, 125)
        )
        
        items.forEach { item ->
            storeRef.child(item.id).setValue(item).await()
        }
    }
    
    private fun generatePartyCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
}
