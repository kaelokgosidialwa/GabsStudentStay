package com.example.gabsstudentstay.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.Date
import java.util.UUID

data class ChatMessage(
    val messageID: String = "",
    val senderID: String = "",
    val message: String = "",
    val timestamp: Date = Date(),
    val read: Boolean = false
)

data class Chat(
    val chatID: String = "",
    val listingID: String = "",
    val tenantID: String = "",
    val lessorID: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Date = Date()
)

object ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val chatsCollection = firestore.collection("chats")

    // generate consistent chatID from listingID and tenantID
    fun getChatID(listingID: String, tenantID: String): String {
        return "${listingID}_${tenantID}"
    }

    // create or get existing chat
    fun getOrCreateChat(
        listingID: String,
        tenantID: String,
        lessorID: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val chatID = getChatID(listingID, tenantID)

        chatsCollection.document(chatID)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // chat already exists
                    onSuccess(chatID)
                } else {
                    // create new chat
                    val chat = Chat(
                        chatID = chatID,
                        listingID = listingID,
                        tenantID = tenantID,
                        lessorID = lessorID,
                        lastMessage = "",
                        lastMessageTime = Date()
                    )
                    chatsCollection.document(chatID)
                        .set(chat)
                        .addOnSuccessListener { onSuccess(chatID) }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Failed to create chat")
                        }
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to get chat")
            }
    }

    // send a message
    fun sendMessage(
        chatID: String,
        senderID: String,
        message: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val messageID = "M_${UUID.randomUUID().toString().take(4).uppercase()}"
        val chatMessage = ChatMessage(
            messageID = messageID,
            senderID = senderID,
            message = message,
            timestamp = Date(),
            read = false
        )

        // save message to subcollection
        chatsCollection
            .document(chatID)
            .collection("messages")
            .document(messageID)
            .set(chatMessage)
            .addOnSuccessListener {
                // update last message on chat document
                chatsCollection.document(chatID)
                    .update(
                        mapOf(
                            "lastMessage" to message,
                            "lastMessageTime" to Date()
                        )
                    )
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to update chat")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to send message")
            }
    }

    // listen to messages in real time
    fun listenToMessages(
        chatID: String,
        onMessagesChanged: (List<ChatMessage>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return chatsCollection
            .document(chatID)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Failed to listen to messages")
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(ChatMessage::class.java)
                } ?: emptyList()
                onMessagesChanged(messages)
            }
    }

    // get all chats for a user
    fun getUserChats(
        userID: String,
        role: String,
        onSuccess: (List<Chat>) -> Unit,
        onError: (String) -> Unit
    ) {
        val field = if (role == "LESSOR") "lessorID" else "tenantID"
        chatsCollection
            .whereEqualTo(field, userID)
            .get()
            .addOnSuccessListener { result ->
                val chats = result.documents.mapNotNull {
                    it.toObject(Chat::class.java)
                }.sortedByDescending { it.lastMessageTime }
                onSuccess(chats)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to get chats")
            }
    }
}