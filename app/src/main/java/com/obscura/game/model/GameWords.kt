package com.obscura.game.model

object GameWords {
    private val wordCategories = mapOf(
        "Animals" to listOf("Lion", "Eagle", "Dolphin", "Elephant", "Tiger", "Penguin", "Giraffe", "Cheetah"),
        "Food" to listOf("Pizza", "Burger", "Sushi", "Pasta", "Tacos", "Ramen", "Curry", "Salad"),
        "Technology" to listOf("Smartphone", "Computer", "Headphones", "Camera", "Drone", "Tablet", "Smartwatch", "Console"),
        "Sports" to listOf("Football", "Basketball", "Tennis", "Baseball", "Hockey", "Cricket", "Volleyball", "Golf"),
        "Movies" to listOf("Action", "Comedy", "Horror", "Drama", "Thriller", "Romance", "Fantasy", "SciFi"),
        "Music" to listOf("Guitar", "Piano", "Drums", "Violin", "Saxophone", "Trumpet", "Flute", "Bass"),
        "Nature" to listOf("Mountain", "Ocean", "Forest", "Desert", "River", "Lake", "Canyon", "Volcano"),
        "Professions" to listOf("Doctor", "Teacher", "Engineer", "Artist", "Chef", "Pilot", "Scientist", "Musician")
    )
    
    fun getRandomWordAndCategory(): Pair<String, String> {
        val category = wordCategories.keys.random()
        val word = wordCategories[category]!!.random()
        return Pair(word, category)
    }
}
