package com.obscura.game.model

data class Player(
    val id: String = "",
    val name: String = "",
    val avatarId: String = "default",
    val accessoryIds: List<String> = emptyList(),
    val coins: Int = 0,
    val isHost: Boolean = false,
    val isReady: Boolean = false
) {
    constructor() : this("", "", "default", emptyList(), 0, false, false)
}
