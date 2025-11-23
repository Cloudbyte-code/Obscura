package com.obscura.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obscura.game.R
import com.obscura.game.model.GameParty
import com.obscura.game.model.Player

@Composable
fun LobbyScreen(
    party: GameParty,
    currentPlayer: Player,
    onStartGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.party_code, party.partyCode),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.waiting_for_players),
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(party.players.values.toList()) { player ->
                PlayerCard(player = player)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (currentPlayer.id == party.hostId && party.players.size >= 3) {
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Start Game",
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = player.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            if (player.isHost) {
                Text(
                    text = "HOST",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
