package com.obscura.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.obscura.game.R
import com.obscura.game.model.Player

@Composable
fun MainMenuScreen(
    player: Player,
    onQuickMatch: () -> Unit,
    onCreateParty: () -> Unit,
    onJoinParty: (String) -> Unit,
    onStore: () -> Unit
) {
    var showJoinDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Welcome, ${player.name}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = stringResource(R.string.your_coins, player.coins),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onQuickMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.quick_match),
                    fontSize = 18.sp
                )
            }
            
            Button(
                onClick = onCreateParty,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_party),
                    fontSize = 18.sp
                )
            }
            
            Button(
                onClick = { showJoinDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.join_party),
                    fontSize = 18.sp
                )
            }
            
            OutlinedButton(
                onClick = onStore,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.store),
                    fontSize = 18.sp
                )
            }
        }
    }
    
    if (showJoinDialog) {
        JoinPartyDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { code ->
                onJoinParty(code)
                showJoinDialog = false
            }
        )
    }
}

@Composable
fun JoinPartyDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var partyCode by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.join_party)) },
        text = {
            OutlinedTextField(
                value = partyCode,
                onValueChange = { partyCode = it.uppercase() },
                label = { Text(stringResource(R.string.enter_party_code)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (partyCode.isNotBlank()) onJoin(partyCode) },
                enabled = partyCode.isNotBlank()
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
