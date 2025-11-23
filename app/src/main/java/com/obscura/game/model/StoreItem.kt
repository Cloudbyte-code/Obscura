package com.obscura.game.model

data class StoreItem(
    val id: String = "",
    val name: String = "",
    val type: ItemType = ItemType.AVATAR,
    val price: Int = 0,
    val imageUrl: String = ""
) {
    constructor() : this("", "", ItemType.AVATAR, 0, "")
}

enum class ItemType {
    AVATAR,
    ACCESSORY
}
