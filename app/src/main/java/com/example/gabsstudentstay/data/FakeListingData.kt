package com.example.gabsstudentstay.data

import com.google.firebase.firestore.GeoPoint
import java.util.Date

object FakeListingData {
    val listings = listOf(
        Listing(
            listingID = "L_1",
            lessorID = "U_1",
            shortDesc = "Cozy studio near UB campus fully furnished",
            longDesc = "A lovely self contained studio apartment located near the University of Botswana campus. Fully furnished with a bed, wardrobe, desk and kitchen area. Water and electricity included in the monthly rent. Safe and secure neighbourhood with 24 hour security.",
            sharedImages = emptyList(),
            sharedDate = Date(),
            totalRooms = 2,
            address = "15 Notwane Road",
            city = "Gaborone",
            position = GeoPoint(-24.6551, 25.9089),
            available = true,
            minPricing = 2500.00,
            maxPricing = 3200.00
        ),
        Listing(
            listingID = "L_2",
            lessorID = "U_2",
            shortDesc = "Spacious shared flat near Game City Mall",
            longDesc = "A spacious shared flat located a short walk from Game City Mall. Three rooms currently available with shared kitchen and two bathrooms. High speed WiFi included. Regular cleaning service provided. Close to public transport links and shops.",
            sharedImages = emptyList(),
            sharedDate = Date(),
            totalRooms = 3,
            address = "42 Tlokweng Road",
            city = "Gaborone",
            position = GeoPoint(-24.6282, 25.9312),
            available = true,
            minPricing = 1800.00,
            maxPricing = 3200.00
        ),
        Listing(
            listingID = "L_3",
            lessorID = "U_3",
            shortDesc = "Modern bachelor flat in Phakalane Estate",
            longDesc = "A modern bachelor flat situated in the quiet and secure Phakalane Estate. Features an open plan lounge and kitchen, one bedroom and one bathroom. Fully furnished with fibre internet included. Ideal for a working professional or postgraduate student.",
            sharedImages = emptyList(),
            sharedDate = Date(),
            totalRooms = 1,
            address = "8 Phakalane Drive",
            city = "Gaborone",
            position = GeoPoint(-24.5933, 25.9187),
            available = true,
            minPricing = 4500.00,
            maxPricing = 4500.00
        )
    )
}