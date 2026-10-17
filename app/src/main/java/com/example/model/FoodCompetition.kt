package com.example.model

data class Contender(
    val id: String, // "A" or "B"
    val spotId: String,
    val spotName: String,
    val dishName: String,
    val description: String,
    val chefQuote: String,
    var votes: Int
)

data class DebateComment(
    val id: String,
    val author: String,
    val authorEmoji: String,
    val contenderPick: String, // "A" or "B"
    val text: String,
    var likes: Int,
    val timeAgo: String
)

data class FoodCompetition(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val badge: String,
    val endsInDays: Int,
    val prizePool: String,
    val contenderA: Contender,
    val contenderB: Contender,
    var userVotedFor: String? = null, // "A" or "B" or null
    val debates: List<DebateComment> = emptyList(),
    val isFeatured: Boolean = false
) {
    val totalVotes: Int get() = contenderA.votes + contenderB.votes
    val percentA: Int get() = if (totalVotes == 0) 50 else ((contenderA.votes.toFloat() / totalVotes) * 100).toInt()
    val percentB: Int get() = 100 - percentA
}

data class SpotNomination(
    val id: String,
    val spotName: String,
    val dishName: String,
    val category: String,
    val justification: String,
    val nominatedBy: String,
    val votes: Int = 1
)
