package com.example.gabsstudentstay.data

import java.util.Date

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    RESERVED
}

data class Booking(
    val bookingID: String = "",
    val tenantID: String = "",
    val roomID: String = "",
    val listingID: String = "",
    val bookingDate: Date = Date(),
    val moveInDate: Date = Date(),
    val moveOutDate: Date = Date(),
    val totalPrice: Int = 0,
    val status: String = BookingStatus.PENDING.name,  // ← String not enum
    val createdAt: Date = Date()
)