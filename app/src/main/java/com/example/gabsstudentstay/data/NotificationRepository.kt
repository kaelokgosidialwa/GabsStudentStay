package com.example.gabsstudentstay.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID

object NotificationRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val notificationsCollection = firestore.collection("notifications")

    // send a notification to a user
    fun sendNotification(
        userID: String,
        title: String,
        message: String,
        type: NotificationType,
        relatedID: String = "",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val notificationID = "N_${UUID.randomUUID().toString().take(4).uppercase()}"
        val notification = AppNotification(
            notificationID = notificationID,
            userID = userID,
            title = title,
            message = message,
            type = type.name,
            read = false,
            createdAt = java.util.Date(),
            relatedID = relatedID
        )

        notificationsCollection
            .document(notificationID)
            .set(notification)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to send notification")
            }
    }

    // fetch all notifications for a user
    fun getNotifications(
        userID: String,
        onSuccess: (List<AppNotification>) -> Unit,
        onError: (String) -> Unit
    ) {
        notificationsCollection
            .whereEqualTo("userID", userID)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val notifications = result.documents.mapNotNull {
                    it.toObject(AppNotification::class.java)
                }
                onSuccess(notifications)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch notifications")
            }
    }

    // mark a notification as read
    fun markAsRead(
        notificationID: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        notificationsCollection
            .document(notificationID)
            .update("read", true)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to mark as read")
            }
    }

    // mark all notifications as read for a user
    fun markAllAsRead(
        userID: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        notificationsCollection
            .whereEqualTo("userID", userID)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { result ->
                val batch = firestore.batch()
                result.documents.forEach { doc ->
                    batch.update(doc.reference, "read", true)
                }
                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to mark all as read")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch notifications")
            }
    }

    // get unread count for a user
    fun getUnreadCount(
        userID: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        notificationsCollection
            .whereEqualTo("userID", userID)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.size())
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to get unread count")
            }
    }

    // send new listing match notifications to matching tenants
    fun notifyMatchingTenants(
        listing: Listing,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        // fetch all tenant users
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("role", "TENANT")
            .get()
            .addOnSuccessListener { result ->
                val tenants = result.documents.mapNotNull {
                    it.toObject(User::class.java)
                }

                // filter tenants whose preferences match the listing
                val matchingTenants = tenants.filter { tenant ->
                    val prefs = tenant.preferences
                    val budgetMatch = listing.minPricing <= prefs.maxBudget
                    val areaMatch = prefs.preferredArea.isBlank() ||
                            listing.city.contains(prefs.preferredArea, ignoreCase = true) ||
                            listing.address.contains(prefs.preferredArea, ignoreCase = true)
                    val keywordMatch = prefs.keywords.isEmpty() ||
                            prefs.keywords.any { keyword ->
                                listing.shortDesc.contains(keyword, ignoreCase = true) ||
                                        listing.longDesc.contains(keyword, ignoreCase = true)
                            }
                    budgetMatch && areaMatch && keywordMatch
                }

                // send notification to each matching tenant
                matchingTenants.forEach { tenant ->
                    sendNotification(
                        userID = tenant.userID,
                        title = "New Listing Match!",
                        message = "A new listing matches your preferences: ${listing.shortDesc}",
                        type = NotificationType.NEW_LISTING_MATCH,
                        relatedID = listing.listingID
                    )
                }

                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to notify tenants")
            }
    }
}