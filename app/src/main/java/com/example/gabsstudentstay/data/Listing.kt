package com.example.gabsstudentstay.data

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Listing(
    // existing fields unchanged
    val listingID: String = "",
    val lessorID: String = "",
    val shortDesc: String = "",
    val longDesc: String = "",
    val sharedImages: List<String> = emptyList(),
    val sharedDate: Date = Date(),
    val totalRooms: Int = 0,
    val address: String = "",
    val city: String = "",
    val position: GeoPoint = GeoPoint(0.0, 0.0),
    val available: Boolean = true,
    val minPricing: Double = 0.0,
    val maxPricing: Double = 0.0,
    val availabilityDate: Date = Date(),
    val tags: List<String> = emptyList()
)