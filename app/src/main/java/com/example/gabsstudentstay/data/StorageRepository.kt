package com.example.gabsstudentstay.data

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

object StorageRepository {

    // ← explicitly use your bucket URL
    private val storage = FirebaseStorage.getInstance("gs://gabs-student-stay-5f5dd.firebasestorage.app")

    fun uploadListingImage(
        listingID: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference
            .child("listings/$listingID/${System.currentTimeMillis()}.jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        onSuccess(downloadUrl.toString())
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to get download URL")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to upload image")
            }
    }

    fun uploadRoomImage(
        listingID: String,
        roomID: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference
            .child("listings/$listingID/rooms/$roomID/${System.currentTimeMillis()}.jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        onSuccess(downloadUrl.toString())
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to get download URL")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to upload image")
            }
    }

    fun uploadProfileImage(
        userID: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference
            .child("users/$userID/profile.jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { downloadUrl ->
                        onSuccess(downloadUrl.toString())
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to get download URL")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to upload image")
            }
    }
}