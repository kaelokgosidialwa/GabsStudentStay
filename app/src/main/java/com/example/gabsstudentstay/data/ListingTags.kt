package com.example.gabsstudentstay.data

object ListingTags {

    val PROPERTY_TYPE = listOf(
        "House",
        "Apartment",
        "Studio",
        "Shared",
        "Bachelor"
    )

    val AMENITIES = listOf(
        "WiFi",
        "Water Included",
        "Electricity Included",
        "Food Included",
        "Furnished",
        "Parking",
        "Security",
        "DSTV"
    )

    val LOCATION = listOf(
        "Near UB",
        "Near BAC",
        "Near Shops",
        "Near Schools",
        "Near Hospital",
        "Near Transport"
    )

    val PRICE_RANGE = listOf(
        "Affordable",    // under P1200
        "Mid Range",     // P1200 - P3000
        "Premium"        // above P3000
    )

    // all tags combined for easy access
    val ALL = PROPERTY_TYPE + AMENITIES + LOCATION + PRICE_RANGE

    // category labels for display
    val CATEGORIES = mapOf(
        "Property Type" to PROPERTY_TYPE,
        "Amenities" to AMENITIES,
        "Location" to LOCATION,
        "Price Range" to PRICE_RANGE
    )

    // auto suggest price tag based on minPricing
    fun suggestPriceTag(minPricing: Double): String {
        return when {
            minPricing < 1200 -> "Affordable"
            minPricing <= 3000 -> "Mid Range"
            else -> "Premium"
        }
    }
}