package com.example.gabsstudentstay.data

object FakeRoomData {
    fun getRoomsForListing(listingID: String): List<Room> {
        return when (listingID) {
            "L_1" -> listOf(
                Room(
                    roomID = "L_1_A",
                    listingID = "L_1",
                    roomNumber = 1,
                    pricing = 2500,
                    capacity = 1,
                    available = true,
                    roomImages = emptyList(),
                    roomDesc = "Self contained single room with en suite bathroom and study desk"
                ),
                Room(
                    roomID = "L_1_B",
                    listingID = "L_1",
                    roomNumber = 2,
                    pricing = 3200,
                    capacity = 2,
                    available = true,
                    roomImages = emptyList(),
                    roomDesc = "Spacious double room with en suite bathroom and built in wardrobe"
                )
            )
            "L_2" -> listOf(
                Room(
                    roomID = "L_2_A",
                    listingID = "L_2",
                    roomNumber = 1,
                    pricing = 1800,
                    capacity = 1,
                    available = true,
                    roomImages = emptyList(),
                    roomDesc = "Single room with study desk and shelving"
                ),
                Room(
                    roomID = "L_2_B",
                    listingID = "L_2",
                    roomNumber = 2,
                    pricing = 3200,
                    capacity = 2,
                    available = true,
                    roomImages = emptyList(),
                    roomDesc = "Large double room with built in wardrobe and dressing area"
                ),
                Room(
                    roomID = "L_2_C",
                    listingID = "L_2",
                    roomNumber = 3,
                    pricing = 2000,
                    capacity = 1,
                    available = false,
                    roomImages = emptyList(),
                    roomDesc = "Cozy single room with garden view"
                )
            )
            "L_3" -> listOf(
                Room(
                    roomID = "L_3_A",
                    listingID = "L_3",
                    roomNumber = 1,
                    pricing = 4500,
                    capacity = 1,
                    available = true,
                    roomImages = emptyList(),
                    roomDesc = "Open plan bachelor room with modern finishes and full kitchen"
                )
            )
            else -> emptyList()
        }
    }
}