package com.example.model

data class UserAccount(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarEmoji: String = "🍜",
    val foodieTitle: String = "Street Food Critic",
    val favoriteCuisines: List<String> = listOf("Street Food", "Artisan Coffee", "Ramen"),
    val xpPoints: Int = 420,
    val level: Int = 4,
    val competitionsVotedCount: Int = 6,
    val checkInsCount: Int = 9,
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
