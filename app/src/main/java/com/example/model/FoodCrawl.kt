package com.example.model

data class FoodCrawl(
    val id: String,
    val title: String,
    val description: String,
    val themeBadge: String,
    val estimatedDuration: String,
    val totalDistance: String,
    val stopSpotIds: List<String>,
    val highlights: List<String>,
    val author: String,
    var completedStops: Int = 0,
    var isStarted: Boolean = false
)
