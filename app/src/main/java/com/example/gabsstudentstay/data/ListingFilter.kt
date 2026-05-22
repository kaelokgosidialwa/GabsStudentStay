package com.example.gabsstudentstay.data

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ListingFilter {

    // coordinates of key locations in Gaborone
    private val UB_LOCATION = Pair(-24.6551, 25.9089)
    private val BAC_LOCATION = Pair(-24.6564, 25.9086)
    private const val NEAR_RADIUS_KM = 2.0

    fun distanceKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }


    // Latest listings - sorted by date newest first
    fun latestListings(listings: List<Listing>): List<Listing> {
        return listings.sortedByDescending { it.sharedDate }
    }

    // Near UB - within 2km of UB
    fun nearUB(listings: List<Listing>): List<Listing> {
        return listings.filter { listing ->
            val lat = listing.position.latitude
            val lon = listing.position.longitude
            distanceKm(lat, lon, UB_LOCATION.first, UB_LOCATION.second) <= NEAR_RADIUS_KM
        }
    }

    // Near BAC - within 2km of BAC
    fun nearBAC(listings: List<Listing>): List<Listing> {
        return listings.filter { listing ->
            val lat = listing.position.latitude
            val lon = listing.position.longitude
            distanceKm(lat, lon, BAC_LOCATION.first, BAC_LOCATION.second) <= NEAR_RADIUS_KM
        }
    }


    // Budget friendly - listings within tenant's max budget
    fun budgetFriendly(
        listings: List<Listing>,
        maxBudget: Int = 2000
    ): List<Listing> {
        return listings.filter { it.maxPricing <= maxBudget }
    }

    // Spacious options - listings with at least one room with capacity >= 2
    fun spaciousOptions(
        listings: List<Listing>,
        rooms: List<Room>
    ): List<Listing> {
        val listingIDsWithSpaceRooms = rooms
            .filter { it.capacity >= 2 }
            .map { it.listingID }
            .toSet()
        return listings.filter { it.listingID in listingIDsWithSpaceRooms }
    }

    // According to preferences
    fun accordingToPreferences(
        listings: List<Listing>,
        rooms: List<Room>,
        preferences: TenantPreferences
    ): List<Listing> {
        return listings.filter { listing ->
            val budgetMatch = listing.minPricing <= preferences.maxBudget
            val areaMatch = preferences.preferredArea.isBlank() ||
                    listing.city.contains(preferences.preferredArea, ignoreCase = true) ||
                    listing.address.contains(preferences.preferredArea, ignoreCase = true)
            val capacityMatch = preferences.minCapacity <= 1 ||
                    rooms.any { room ->
                        room.listingID == listing.listingID &&
                                room.capacity >= preferences.minCapacity
                    }
            val keywordMatch = preferences.keywords.isEmpty() ||
                    preferences.keywords.any { keyword ->
                        listing.shortDesc.contains(keyword, ignoreCase = true) ||
                                listing.longDesc.contains(keyword, ignoreCase = true)
                    }
            val tagMatch = preferences.preferredTags.isEmpty() ||
                    preferences.preferredTags.all { tag ->
                        listing.tags.contains(tag)
                    }

            budgetMatch && areaMatch && capacityMatch && keywordMatch && tagMatch
        }
    }

    fun availableOnly(listings: List<Listing>): List<Listing> {
        return listings.filter { it.available }
    }

    fun removeFullyBooked(
        listings: List<Listing>,
        rooms: List<Room>
    ): List<Listing> {
        return listings.filter { listing ->
            val listingRooms = rooms.filter { it.listingID == listing.listingID }
            listingRooms.isEmpty() || listingRooms.any { it.available }
        }
    }

}