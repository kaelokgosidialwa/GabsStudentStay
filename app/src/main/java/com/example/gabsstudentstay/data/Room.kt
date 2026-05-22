package com.example.gabsstudentstay.data

data class Room(
    val roomID: String = "",
    val listingID: String = "",
    val roomNumber: Int = 0,
    val pricing: Int = 0,
    val capacity: Int = 0,
    val available: Boolean = true,
    val status: String = "AVAILABLE",  // ← NEW: AVAILABLE, RESERVED
    val roomImages: List<String> = emptyList(),
    val roomDesc: String = ""
)