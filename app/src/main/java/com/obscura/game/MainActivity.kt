package com.obscura.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.obscura.game.ui.screens.*
import com.obscura.game.ui.theme.ObscuraTheme
import com.obscura.game.viewmodel.GameViewModel
import com.obscura.game.viewmodel.NavigationState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ObscuraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ObscuraApp()
                }
            }
        }
    }
}

@Composable
fun ObscuraApp(viewModel: GameViewModel = viewModel()) {
    val navigationState by viewModel.navigationState.collectAsState()
    val currentPlayer by viewModel.currentPlayer.collectAsState()
    val currentParty by viewModel.currentParty.collectAsState()
    val storeItems by viewModel.storeItems.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Load store items on first composition
    LaunchedEffect(Unit) {
        viewModel.loadStoreItems()
    }
    
    // Show error messages
    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
    
    when (navigationState) {
        NavigationState.Home -> {
            HomeScreen(
                onCreateCharacter = { name ->
                    viewModel.createCharacter(name)
                }
            )
        }
        
        NavigationState.MainMenu -> {
            currentPlayer?.let { player ->
                MainMenuScreen(
                    player = player,
                    onQuickMatch = { viewModel.quickMatch() },
                    onCreateParty = { viewModel.createParty() },
                    onJoinParty = { code -> viewModel.joinParty(code) },
                    onStore = { viewModel.navigateTo(NavigationState.Store) }
                )
            }
        }
        
        NavigationState.Lobby -> {
            currentParty?.let { party ->
                currentPlayer?.let { player ->
                    LobbyScreen(
                        party = party,
                        currentPlayer = player,
                        onStartGame = { viewModel.startGame() }
                    )
                }
            }
        }
        
        NavigationState.HintRound -> {
            currentParty?.let { party ->
                currentPlayer?.let { player ->
                    HintRoundScreen(
                        party = party,
                        currentPlayer = player,
                        onSubmitHint = { hint ->
                            viewModel.submitHint(hint)
                        },
                        onMoveToVoting = { viewModel.moveToVoting() }
                    )
                }
            }
        }
        
        NavigationState.Voting -> {
            currentParty?.let { party ->
                currentPlayer?.let { player ->
                    VotingScreen(
                        party = party,
                        currentPlayer = player,
                        onVote = { votedPlayerId ->
                            viewModel.submitVote(votedPlayerId)
                        }
                    )
                }
            }
        }
        
        NavigationState.GameOver -> {
            currentParty?.let { party ->
                currentPlayer?.let { player ->
                    GameOverScreen(
                        party = party,
                        currentPlayer = player,
                        onBackToMenu = {
                            viewModel.navigateTo(NavigationState.MainMenu)
                        }
                    )
                }
            }
        }
        
        NavigationState.Store -> {
            currentPlayer?.let { player ->
                StoreScreen(
                    player = player,
                    items = storeItems,
                    onBuyItem = { item ->
                        viewModel.buyItem(item)
                    },
                    onBack = { viewModel.navigateTo(NavigationState.MainMenu) }
                )
            }
        }
    }
}
