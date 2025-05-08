package com.moviles.clothingapp.chat

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.moviles.clothingapp.chat.data.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val db = FirebaseFirestore.getInstance()

    fun sendMessage(senderId: String, receiverId: String, text: String) {
        val conversationId = getConversationId(senderId, receiverId)
        val message = ChatMessage(senderId, receiverId, text)

        // For sender
        db.collection("users").document(senderId)
            .collection("chat_overview")
            .document(receiverId)
            .set(
                mapOf(
                    "chatPartnerId" to receiverId,
                    "lastMessage" to text,
                    "timestamp" to System.currentTimeMillis()
                )
            )

        // For receiver
        db.collection("users").document(receiverId)
            .collection("chat_overview")
            .document(senderId)
            .set(
                mapOf(
                    "chatPartnerId" to senderId,
                    "lastMessage" to text,
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }

    fun loadMessages(senderId: String, receiverId: String) {
        val conversationId = getConversationId(senderId, receiverId)

        db.collection("chats")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val msgs = snapshot.toObjects(ChatMessage::class.java)
                    _messages.value = msgs
                }
            }
    }

    private fun getConversationId(uid1: String, uid2: String): String {
        return listOf(uid1, uid2).sorted().joinToString("_")
    }
}
