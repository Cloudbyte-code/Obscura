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
fun HintRoundScreen(
    party: GameParty,
    currentPlayer: Player,
    onSubmitHint: (String) -> Unit,
    onMoveToVoting: () -> Unit
) {
    var hint by remember { mutableStateOf("") }
    val isImposter = currentPlayer.id == party.imposterId
    val hasSubmittedHint = party.hints[currentPlayer.id]?.size ?: 0 >= party.currentRound
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.round, party.currentRound),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isImposter) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.you_are_imposter),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.category, party.category),
                        fontSize = 18.sp
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.your_word, party.word),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.category, party.category),
                        fontSize = 16.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.give_hint),
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = hint,
            onValueChange = { hint = it },
            label = { Text(stringResource(R.string.enter_hint)) },
            enabled = !hasSubmittedHint,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                if (hint.isNotBlank()) {
                    onSubmitHint(hint)
                    hint = ""
                }
            },
            enabled = hint.isNotBlank() && !hasSubmittedHint,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = if (hasSubmittedHint) "Hint Submitted" else stringResource(R.string.submit_hint),
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Hints from players:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            party.hints.forEach { (playerId, hints) ->
                val player = party.players[playerId]
                if (player != null && hints.isNotEmpty()) {
                    item {
                        HintCard(
                            playerName = player.name,
                            hints = hints
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (currentPlayer.id == party.hostId) {
            val allSubmitted = party.players.keys.all { playerId ->
                (party.hints[playerId]?.size ?: 0) >= party.currentRound
            }
            
            if (allSubmitted) {
                Button(
                    onClick = onMoveToVoting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Move to Voting",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HintCard(playerName: String, hints: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = playerName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            hints.forEach { hint ->
                Text(
                    text = "• $hint",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }
    }
}
