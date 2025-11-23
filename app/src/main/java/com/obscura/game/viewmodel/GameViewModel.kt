package com.obscura.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obscura.game.data.FirebaseRepository
import com.obscura.game.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _currentPlayer = MutableStateFlow<Player?>(null)
    val currentPlayer: StateFlow<Player?> = _currentPlayer.asStateFlow()
    
    private val _currentParty = MutableStateFlow<GameParty?>(null)
    val currentParty: StateFlow<GameParty?> = _currentParty.asStateFlow()
    
    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Home)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()
    
    private val _storeItems = MutableStateFlow<List<StoreItem>>(emptyList())
    val storeItems: StateFlow<List<StoreItem>> = _storeItems.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun createCharacter(name: String) {
        viewModelScope.launch {
            try {
                val player = repository.createPlayer(name)
                _currentPlayer.value = player
                _navigationState.value = NavigationState.MainMenu
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create character: ${e.message}"
            }
        }
    }
    
    fun createParty() {
        viewModelScope.launch {
            try {
                val player = _currentPlayer.value ?: return@launch
                val partyCode = repository.createParty(player)
                observeParty(partyCode)
                _navigationState.value = NavigationState.Lobby
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create party: ${e.message}"
            }
        }
    }
    
    fun joinParty(partyCode: String) {
        viewModelScope.launch {
            try {
                val player = _currentPlayer.value ?: return@launch
                val success = repository.joinParty(partyCode, player)
                if (success) {
                    observeParty(partyCode)
                    _navigationState.value = NavigationState.Lobby
                } else {
                    _errorMessage.value = "Failed to join party. Party may be full or in progress."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to join party: ${e.message}"
            }
        }
    }
    
    fun quickMatch() {
        viewModelScope.launch {
            try {
                val player = _currentPlayer.value ?: return@launch
                val partyCode = repository.joinMatchmaking(player)
                if (partyCode != null) {
                    observeParty(partyCode)
                    _navigationState.value = NavigationState.Lobby
                } else {
                    _errorMessage.value = "Failed to find a match"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to quick match: ${e.message}"
            }
        }
    }
    
    fun startGame() {
        viewModelScope.launch {
            try {
                val partyCode = _currentParty.value?.partyCode ?: return@launch
                repository.startGame(partyCode)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to start game: ${e.message}"
            }
        }
    }
    
    fun submitHint(hint: String) {
        viewModelScope.launch {
            try {
                val player = _currentPlayer.value ?: return@launch
                val partyCode = _currentParty.value?.partyCode ?: return@launch
                repository.submitHint(partyCode, player.id, hint)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to submit hint: ${e.message}"
            }
        }
    }
    
    fun submitVote(votedPlayerId: String) {
        viewModelScope.launch {
            try {
                val player = _currentPlayer.value ?: return@launch
                val partyCode = _currentParty.value?.partyCode ?: return@launch
                repository.submitVote(partyCode, player.id, votedPlayerId)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to submit vote: ${e.message}"
            }
        }
    }
    
    fun moveToVoting() {
        viewModelScope.launch {
            try {
                val partyCode = _currentParty.value?.partyCode ?: return@launch
                repository.moveToVoting(partyCode)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to move to voting: ${e.message}"
            }
        }
    }
    
    fun loadStoreItems() {
        viewModelScope.launch {
            try {
                val items = repository.getStoreItems()
                if (items.isEmpty()) {
                    repository.initializeStore()
                    _storeItems.value = repository.getStoreItems()
                } else {
                    _storeItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load store: ${e.message}"
            }
        }
    }
    
    fun buyItem(item: StoreItem) {
        viewModelScope.launch {
            try {
                val player = _currentPlayer.value ?: return@launch
                if (player.coins >= item.price) {
                    repository.updatePlayerCoins(player.id, player.coins - item.price)
                    when (item.type) {
                        ItemType.AVATAR -> repository.updatePlayerAvatar(player.id, item.id)
                        ItemType.ACCESSORY -> repository.addPlayerAccessory(player.id, item.id)
                    }
                    val updatedPlayer = repository.getPlayer(player.id)
                    _currentPlayer.value = updatedPlayer
                } else {
                    _errorMessage.value = "Not enough coins"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to buy item: ${e.message}"
            }
        }
    }
    
    fun navigateTo(state: NavigationState) {
        _navigationState.value = state
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    private fun observeParty(partyCode: String) {
        viewModelScope.launch {
            repository.observeParty(partyCode).collect { party ->
                _currentParty.value = party
                
                // Auto-navigate based on game state
                party?.let {
                    when (it.gameState) {
                        GameState.WAITING -> _navigationState.value = NavigationState.Lobby
                        GameState.HINT_ROUND -> _navigationState.value = NavigationState.HintRound
                        GameState.VOTING -> _navigationState.value = NavigationState.Voting
                        GameState.GAME_OVER -> _navigationState.value = NavigationState.GameOver
                        else -> {}
                    }
                }
            }
        }
    }
}

sealed class NavigationState {
    object Home : NavigationState()
    object MainMenu : NavigationState()
    object Lobby : NavigationState()
    object HintRound : NavigationState()
    object Voting : NavigationState()
    object GameOver : NavigationState()
    object Store : NavigationState()
}
