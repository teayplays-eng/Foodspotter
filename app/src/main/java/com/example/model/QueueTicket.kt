package com.example.model

enum class TicketStatus(val title: String) {
    WAITING("In Queue"),
    CALLING("Ready to Be Seated!"),
    SEATED("Seated"),
    CANCELLED("Cancelled")
}

data class QueueTicket(
    val ticketId: String,
    val spotId: String,
    val spotName: String,
    val spotAddress: String,
    val guestName: String,
    val partySize: Int,
    val seatingPreference: String, // "Indoor", "Outdoor Patio", "Any"
    val initialPosition: Int,
    var currentPosition: Int,
    var estimatedWaitMinutes: Int,
    var status: TicketStatus = TicketStatus.WAITING,
    val timestamp: Long = System.currentTimeMillis()
)
