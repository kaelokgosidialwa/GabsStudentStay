package com.example.gabsstudentstay.data

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object BookingRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getTenantBookings(
        tenantID: String,
        onSuccess: (List<Booking>) -> Unit,
        onError: (String) -> Unit
    ) {
        firestore.collection("bookings")
            .whereEqualTo("tenantID", tenantID)
            .get()
            .addOnSuccessListener { result ->
                val bookings = result.documents
                    .mapNotNull { it.toObject(Booking::class.java) }
                    .sortedByDescending { it.createdAt }
                onSuccess(bookings)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch bookings")
            }
    }

    fun cancelBooking(
        booking: Booking,
        cancelledByTenant: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        firestore.collection("bookings")
            .document(booking.bookingID)
            .update("status", BookingStatus.CANCELLED.name)
            .addOnSuccessListener {

                firestore.collection("rooms")
                    .document(booking.roomID)
                    .update("available", true)
                    .addOnSuccessListener {

                        firestore.collection("listings")
                            .document(booking.listingID)
                            .get()
                            .addOnSuccessListener { doc ->
                                val listing = doc.toObject(Listing::class.java)
                                if (listing != null && cancelledByTenant) {

                                    NotificationRepository.sendNotification(
                                        userID = listing.lessorID,
                                        title = "Booking Cancelled",
                                        message = "A tenant has cancelled their booking.",
                                        type = NotificationType.BOOKING_CANCELLED,
                                        relatedID = booking.bookingID
                                    )
                                }
                                onSuccess()
                            }
                            .addOnFailureListener { onSuccess() }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to update room")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to cancel booking")
            }
    }
}