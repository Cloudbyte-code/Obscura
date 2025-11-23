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

@Composable
fun HomeScreen(onCreateCharacter: (String) -> Unit) {
    var playerName by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_to_obscura),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            OutlinedTextField(
                value = playerName,
                onValueChange = { playerName = it },
                label = { Text(stringResource(R.string.enter_your_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = { if (playerName.isNotBlank()) onCreateCharacter(playerName) },
                enabled = playerName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_character),
                    fontSize = 18.sp
                )
            }
        }
    }
}
