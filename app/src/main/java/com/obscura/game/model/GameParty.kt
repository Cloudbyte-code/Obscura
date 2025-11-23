package com.obscura.game.model

data class GameParty(
    val partyCode: String = "",
    val hostId: String = "",
    val players: Map<String, Player> = emptyMap(),
    val gameState: GameState = GameState.WAITING,
    val currentRound: Int = 0,
    val maxRounds: Int = 3,
    val word: String = "",
    val category: String = "",
    val imposterId: String = "",
    val hints: Map<String, List<String>> = emptyMap(), // playerId -> list of hints
    val votes: Map<String, String> = emptyMap(), // voterId -> votedPlayerId
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", emptyMap(), GameState.WAITING, 0, 3, "", "", "", emptyMap(), emptyMap(), System.currentTimeMillis())
}

enum class GameState {
    WAITING,
    STARTING,
    HINT_ROUND,
    VOTING,
    GAME_OVER
}
