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
                _errorMessage.value = "Unable to create character. Please check your connection and try again."
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
                _errorMessage.value = "Unable to create party. Please try again."
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
                    _errorMessage.value = "Unable to join party. It may be full or already started."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error. Please check the party code and try again."
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
                    _errorMessage.value = "Unable to find a match. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error. Please check your network and try again."
            }
        }
    }
    
    fun startGame() {
        viewModelScope.launch {
            try {
                val partyCode = _currentParty.value?.partyCode ?: return@launch
                repository.startGame(partyCode)
            } catch (e: Exception) {
                _errorMessage.value = "Unable to start game. Please ensure you have enough players."
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
                _errorMessage.value = "Unable to submit hint. Please try again."
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
                _errorMessage.value = "Unable to submit vote. Please try again."
            }
        }
    }
    
    fun moveToVoting() {
        viewModelScope.launch {
            try {
                val partyCode = _currentParty.value?.partyCode ?: return@launch
                repository.moveToVoting(partyCode)
            } catch (e: Exception) {
                _errorMessage.value = "Unable to proceed to voting. Please try again."
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
                _errorMessage.value = "Unable to load store. Please check your connection."
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
                    _errorMessage.value = "Not enough coins to purchase this item."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Unable to complete purchase. Please try again."
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
