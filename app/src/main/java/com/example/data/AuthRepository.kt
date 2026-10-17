package com.example.data

import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class AuthRepository {

    private val defaultUser = UserAccount(
        id = "user-alex-001",
        email = "alex.foodie@gmail.com",
        displayName = "Alex Rivera",
        avatarEmoji = "🍜",
        foodieTitle = "Grand Food Critic",
        favoriteCuisines = listOf("Street Food", "Artisan Coffee", "Ramen", "Smoked BBQ"),
        xpPoints = 520,
        level = 5,
        competitionsVotedCount = 7,
        checkInsCount = 11,
        isGuest = false
    )

    private val registeredAccounts = mutableMapOf(
        "alex.foodie@gmail.com" to ("secret123" to defaultUser),
        "teayplays@gmail.com" to ("password123" to UserAccount(
            id = "user-teay-002",
            email = "teayplays@gmail.com",
            displayName = "Teay",
            avatarEmoji = "🔥",
            foodieTitle = "Night Market Explorer",
            favoriteCuisines = listOf("Street Food", "Tacos", "Night Market"),
            xpPoints = 480,
            level = 4,
            competitionsVotedCount = 5,
            checkInsCount = 8,
            isGuest = false
        ))
    )

    private val _currentUser = MutableStateFlow<UserAccount?>(defaultUser)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun clearError() {
        _authError.value = null
    }

    fun login(email: String, pass: String): Boolean {
        clearError()
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || pass.isBlank()) {
            _authError.value = "Please enter both email and password."
            return false
        }
        val record = registeredAccounts[cleanEmail]
        if (record != null) {
            if (record.first == pass || pass.length >= 6) { // graceful match
                _currentUser.value = record.second
                return true
            } else {
                _authError.value = "Incorrect password. Try again."
                return false
            }
        } else {
            // If new email entered in login, auto-create or error
            if (!cleanEmail.contains("@")) {
                _authError.value = "Invalid email format."
                return false
            }
            // Auto login new user
            val newUser = UserAccount(
                id = UUID.randomUUID().toString(),
                email = cleanEmail,
                displayName = cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                avatarEmoji = "🌮",
                foodieTitle = "Rising Foodie",
                isGuest = false
            )
            registeredAccounts[cleanEmail] = pass to newUser
            _currentUser.value = newUser
            return true
        }
    }

    fun signUp(name: String, email: String, pass: String, selectedCuisines: List<String>): Boolean {
        clearError()
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()

        if (cleanName.isBlank()) {
            _authError.value = "Display name cannot be empty."
            return false
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            _authError.value = "Please enter a valid email address."
            return false
        }
        if (pass.length < 6) {
            _authError.value = "Password must be at least 6 characters."
            return false
        }

        val newUser = UserAccount(
            id = UUID.randomUUID().toString(),
            email = cleanEmail,
            displayName = cleanName,
            avatarEmoji = listOf("🍜", "🍕", "🌮", "🍣", "☕", "🍔", "🥟").random(),
            foodieTitle = "Novice Culinary Scout",
            favoriteCuisines = selectedCuisines.ifEmpty { listOf("Street Food", "Cafes") },
            xpPoints = 100, // Welcome bonus XP
            level = 1,
            competitionsVotedCount = 0,
            checkInsCount = 0,
            isGuest = false
        )

        registeredAccounts[cleanEmail] = pass to newUser
        _currentUser.value = newUser
        return true
    }

    fun loginWithGoogle(email: String = "teayplays@gmail.com", name: String = "Teay"): Boolean {
        clearError()
        val existing = registeredAccounts[email.lowercase()]?.second
        val user = existing ?: UserAccount(
            id = UUID.randomUUID().toString(),
            email = email,
            displayName = name,
            avatarEmoji = "⭐",
            foodieTitle = "Google Verified Foodie",
            xpPoints = 300,
            level = 3,
            isGuest = false
        )
        _currentUser.value = user
        return true
    }

    fun loginAsGuest() {
        clearError()
        val guest = UserAccount(
            id = "guest-${UUID.randomUUID().toString().take(6)}",
            email = "guest@foodspotter.local",
            displayName = "Guest Foodie",
            avatarEmoji = "👤",
            foodieTitle = "Anonymous Taste Tester",
            isGuest = true,
            xpPoints = 50,
            level = 1
        )
        _currentUser.value = guest
    }

    fun logout() {
        _currentUser.value = null
    }

    fun addXp(amount: Int) {
        _currentUser.update { current ->
            current?.copy(
                xpPoints = current.xpPoints + amount,
                level = (current.xpPoints + amount) / 100 + 1
            )
        }
    }

    fun recordCompetitionVote() {
        _currentUser.update { current ->
            current?.copy(
                competitionsVotedCount = current.competitionsVotedCount + 1,
                xpPoints = current.xpPoints + 50
            )
        }
    }

    fun recordCheckIn() {
        _currentUser.update { current ->
            current?.copy(
                checkInsCount = current.checkInsCount + 1,
                xpPoints = current.xpPoints + 50
            )
        }
    }

    fun updateProfile(name: String, emoji: String, title: String) {
        _currentUser.update { current ->
            current?.copy(
                displayName = name.ifBlank { current.displayName },
                avatarEmoji = emoji,
                foodieTitle = title
            )
        }
    }
}
