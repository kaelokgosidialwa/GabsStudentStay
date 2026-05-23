package com.example.gabsstudentstay.data

import java.util.Date

enum class NotificationType {
    BOOKING_MADE,
    BOOKING_ACCEPTED,
    BOOKING_DENIED,
    BOOKING_CANCELLED,
    LISTING_DELETED,
    NEW_LISTING_MATCH
}

data class AppNotification(
    val notificationID: String = "",
    val userID: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = NotificationType.BOOKING_MADE.name,
    val read: Boolean = false,
    val createdAt: Date = Date(),
    val relatedID: String = ""
)