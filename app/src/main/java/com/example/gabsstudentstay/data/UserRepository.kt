package com.example.gabsstudentstay.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

object UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun signUp(
        email: String,
        password: String,
        name: String,
        username: String,
        role: UserRole,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        usersCollection
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    onError("Username already taken")
                    return@addOnSuccessListener
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val userID = authResult.user?.uid ?: return@addOnSuccessListener
                        val newUser = User(
                            userID = userID,
                            email = email,
                            username = username,
                            name = name,
                            role = role.name,
                            createdAt = Date()
                        )
                        usersCollection.document(userID)
                            .set(newUser)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e ->
                                onError(e.message ?: "Failed to save user")
                            }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Sign up failed")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to check username")
            }
    }

    fun signIn(
        emailOrUsername: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (emailOrUsername.contains("@")) {
            auth.signInWithEmailAndPassword(emailOrUsername, password)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Sign in failed")
                }
        } else {
            usersCollection
                .whereEqualTo("username", emailOrUsername)
                .get()
                .addOnSuccessListener { result ->
                    if (result.isEmpty) {
                        onError("No account found with that username")
                        return@addOnSuccessListener
                    }
                    val email = result.documents.first().getString("email") ?: ""
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Sign in failed")
                        }
                }
                .addOnFailureListener { e ->
                    onError(e.message ?: "Failed to find account")
                }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUser(
        onSuccess: (User?) -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            onSuccess(null)
            return
        }
        usersCollection.document(firebaseUser.uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onSuccess(user)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to get user")
            }
    }

    fun getUserByID(
        userID: String,
        onSuccess: (User?) -> Unit,
        onError: (String) -> Unit
    ) {
        usersCollection
            .document(userID)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onSuccess(user)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch user")
            }
    }

    fun updateProfile(
        name: String,
        phone: String,
        profileImage: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            onError("No user logged in")
            return
        }
        usersCollection
            .document(firebaseUser.uid)
            .update(
                mapOf(
                    "name" to name,
                    "phone" to phone,
                    "profileImage" to profileImage
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to update profile")
            }
    }

    fun savePreferences(
        preferences: TenantPreferences,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            onError("No user logged in")
            return
        }
        usersCollection
            .document(firebaseUser.uid)
            .update("preferences", preferences)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to save preferences")
            }
    }
}