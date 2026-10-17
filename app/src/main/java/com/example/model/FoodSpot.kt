package com.example.model

enum class FoodCategory(val displayName: String, val iconName: String) {
    ALL("All Spots", "restaurant"),
    STREET_FOOD("Street Food", "fastfood"),
    CAFE("Cozy Cafes", "local_cafe"),
    ICONIC_RESTAURANT("Iconic Eats", "star"),
    NIGHT_MARKET("Night Market", "nightlife"),
    NOODLE_BAR("Ramen & Bowls", "ramen_dining"),
    DESSERT_BAKERY("Bakery & Sweets", "bakery_dining")
}

enum class QueueLevel(val label: String, val badgeText: String) {
    NO_WAIT("Walk-in Available", "⚡ No Wait"),
    FAST("5–15 mins", "⚡ Fast Moving"),
    MODERATE("15–30 mins", "⏳ Moderate Wait"),
    BUSY("35–60 mins", "🔥 Peak Rush")
}

enum class PriceLevel(val symbol: String) {
    BUDGET("$"),
    MODERATE("$$"),
    UPSCALE("$$$")
}

data class Coordinates(
    val lat: Double,
    val lng: Double,
    val mapX: Float, // 0.0f .. 1.0f relative map canvas coordinates
    val mapY: Float
)

data class SignatureDish(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val spiceLevel: Int = 0, // 0 to 3
    val isVegetarian: Boolean = false,
    val isChefSpecial: Boolean = false
)

data class FoodieReview(
    val id: String,
    val authorName: String,
    val authorBadge: String, // e.g. "Top 1% Foodie", "Local Explorer"
    val rating: Float,
    val timeAgo: String,
    val comment: String,
    val highlightedDish: String? = null,
    val likesCount: Int = 0
)

data class FoodSpot(
    val id: String,
    val name: String,
    val tagline: String,
    val category: FoodCategory,
    val rating: Float,
    val reviewCount: Int,
    val priceLevel: PriceLevel,
    val distanceKm: Double,
    val queueLevel: QueueLevel,
    val estimatedWaitMinutes: Int,
    val queuePartiesCount: Int,
    val address: String,
    val district: String,
    val openingHours: String,
    val isOpenNow: Boolean = true,
    val isBookmarked: Boolean = false,
    val checkInCount: Int = 142,
    val coordinates: Coordinates,
    val signatureDishes: List<SignatureDish>,
    val reviews: List<FoodieReview>,
    val foodieTip: String,
    val specialTags: List<String>,
    val drawableResName: String? = null
)
