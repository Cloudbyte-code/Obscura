package com.obscura.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obscura.game.R
import com.obscura.game.model.GameConstants
import com.obscura.game.model.GameParty
import com.obscura.game.model.Player

@Composable
fun GameOverScreen(
    party: GameParty,
    currentPlayer: Player,
    onBackToMenu: () -> Unit
) {
    val imposter = party.players[party.imposterId]
    val isImposter = currentPlayer.id == party.imposterId
    
    // Determine winner based on votes
    val voteCount = party.votes.values.groupingBy { it }.eachCount()
    val mostVoted = voteCount.maxByOrNull { it.value }?.key
    val imposterCaught = mostVoted == party.imposterId
    
    val didWin = if (isImposter) !imposterCaught else imposterCaught
    val coinsEarned = if (didWin) {
        if (isImposter) GameConstants.COINS_FOR_IMPOSTER_WIN else GameConstants.COINS_FOR_INNOCENT_WIN
    } else 0
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (didWin) stringResource(R.string.game_won) else stringResource(R.string.game_lost),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = if (didWin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imposterCaught) {
                    Text(
                        text = stringResource(R.string.imposter_found),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = stringResource(R.string.imposter_escaped),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "The Imposter was: ${imposter?.name ?: "Unknown"}",
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(R.string.coins_earned, coinsEarned),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onBackToMenu,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Back to Menu",
                fontSize = 18.sp
            )
        }
    }
}
