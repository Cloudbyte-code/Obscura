package com.obscura.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun VotingScreen(
    party: GameParty,
    currentPlayer: Player,
    onVote: (String) -> Unit
) {
    val hasVoted = party.votes.containsKey(currentPlayer.id)
    var selectedPlayerId by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.vote_imposter),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (hasVoted) {
            Text(
                text = stringResource(R.string.waiting_for_votes),
                fontSize = 18.sp
            )
            
            Text(
                text = "${party.votes.size} / ${party.players.size} voted",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                text = "Select who you think is the Imposter:",
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(party.players.values.filter { it.id != currentPlayer.id }.toList()) { player ->
                VotePlayerCard(
                    player = player,
                    isSelected = selectedPlayerId == player.id,
                    enabled = !hasVoted,
                    onClick = { selectedPlayerId = player.id }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                selectedPlayerId?.let { onVote(it) }
            },
            enabled = selectedPlayerId != null && !hasVoted,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (hasVoted) "Vote Submitted" else "Submit Vote",
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun VotePlayerCard(
    player: Player,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = { if (enabled) onClick() },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        enabled = enabled
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
            
            if (isSelected) {
                Text(
                    text = "✓",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
