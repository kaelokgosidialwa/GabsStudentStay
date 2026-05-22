package com.example.gabsstudentstay.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

object ListingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val listingsCollection = firestore.collection("listings")

    // Fetch all listings
    fun getListings(
        onSuccess: (List<Listing>) -> Unit,
        onError: (String) -> Unit
    ) {
        listingsCollection
            .get()
            .addOnSuccessListener { result ->
                val listings = result.documents.mapNotNull { document ->
                    document.toObject(Listing::class.java)
                }
                onSuccess(listings)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch listings")
            }
    }

    // Fetch listings by lessorID
    fun getListingsByLessor(
        lessorID: String,
        onSuccess: (List<Listing>) -> Unit,
        onError: (String) -> Unit
    ) {
        listingsCollection
            .whereEqualTo("lessorID", lessorID)
            .get()
            .addOnSuccessListener { result ->
                val listings = result.documents.mapNotNull { document ->
                    document.toObject(Listing::class.java)
                }
                onSuccess(listings)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch listings")
            }
    }

    // Fetch single listing by ID
    fun getListingByID(
        listingID: String,
        onSuccess: (Listing?) -> Unit,
        onError: (String) -> Unit
    ) {
        listingsCollection
            .document(listingID)
            .get()
            .addOnSuccessListener { document ->
                val listing = document.toObject(Listing::class.java)
                onSuccess(listing)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch listing")
            }
    }

    // Add a single listing
    fun addListing(
        listing: Listing,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        listingsCollection
            .document(listing.listingID)
            .set(listing)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to add listing")
            }
    }
}
