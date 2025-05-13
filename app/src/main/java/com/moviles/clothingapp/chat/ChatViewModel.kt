package com.moviles.clothingapp.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.moviles.clothingapp.chat.data.ChatMessage
import com.moviles.clothingapp.chat.data.ChatOverview
import com.moviles.clothingapp.ui.utils.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID


/* Needs to be instanced with factory as each chat changes based on chat partner */
class ChatViewModel(private val chatPartnerId: String) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val productApiService = RetrofitInstance.apiService

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var messagesListener: ListenerRegistration? = null

    private val _productName = MutableStateFlow<String?>(null)
    val productName: StateFlow<String?> = _productName

    init {
        loadMessages()
    }


    /****** Load product name from backend if available ******/
    fun loadProductName(productId: Int) {
        if (productId > 0) {
            viewModelScope.launch {
                try {
                    val response = productApiService.fetchClothesById(productId)
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            _productName.value = product.name
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel ERROR", "Error fetching product name", e)
                }
            }
        }
    }

    /****** Store product ID in chat metadata ******/
    fun initializeConversation(productId: Int) {
        if (productId > 0) {
            val currentUserId = auth.currentUser?.uid ?: return
            val chatId = getChatId(currentUserId, chatPartnerId)
            firestore.collection("chats")
                .document(chatId)
                .set(mapOf("productId" to productId), SetOptions.merge())
        }
    }

    /****** Load product name from firebase if not available in backend ******/
    fun loadConversationName() {
        val currentUserId = auth.currentUser?.uid ?: return
        /* Create a unique chat ID from the two user IDs */
        val chatId = getChatId(currentUserId, chatPartnerId)

        firestore.collection("chats")
            .document(chatId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.contains("productId")) {
                    val productId = document.getLong("productId")?.toInt()
                    if (productId != null && productId > 0) {
                        loadProductName(productId)
                    }
                }
            }
    }


    /****** Load messages from firebase ******/
    fun loadMessages() {
        val currentUserId = auth.currentUser?.uid ?: return
        _isLoading.value = true

        /* Create a unique chat ID from the two user IDs */
        val chatId = getChatId(currentUserId, chatPartnerId)

        messagesListener?.remove()
        messagesListener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                _isLoading.value = false

                if (snapshot != null) {
                    val messagesList = snapshot.toObjects(ChatMessage::class.java)
                    _messages.value = messagesList
                    Log.d("ChatViewModel", "Num of msgs loaded: ${messagesList.size}")
                }
            }
    }

    /****** Send message to firebase ******/
    fun sendMessage(messageText: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        /* Create a unique chat ID from the two user IDs */
        val chatId = getChatId(currentUserId, chatPartnerId)

        /* IMPORTANT: Same variables in firebase */
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = currentUserId,
            receiverId = chatPartnerId,
            text = messageText,
            timestamp = Date()
        )

        /* Send message to the chat collection (between the two users) to firebase store */
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(message.id)
            .set(message)
            .addOnSuccessListener {
                /* Update instantly chat for both users */
                updateChat(currentUserId, chatPartnerId, message)
                updateChat(chatPartnerId, currentUserId, message)
            }
            .addOnFailureListener { e ->
                Log.e("ERROR ChatViewModel", "Error sending message", e)
            }
    }

    /****** Update instantly chat for both users ******/
    private fun updateChat(userId: String, partnerId: String, lastMessage: ChatMessage) {
        val chatOverview = ChatOverview(
            chatPartnerId = partnerId,
            lastMessage = lastMessage.text,
            timestamp = lastMessage.timestamp,
            productId = 1
        )

        firestore.collection("users")
            .document(userId)
            .collection("chats")
            .document(partnerId)
            .set(chatOverview)
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error updating chat for $userId", e)
            }
    }

    private fun getChatId(userId1: String, userId2: String): String {
        /* Ensure same chat for both users (no duplicates and same chat id regardless of which is actual user) */
        return if (userId1 < userId2) {
            "${userId1}_${userId2}"
        } else {
            "${userId2}_${userId1}"
        }
    }
}

/********* Factory for creating the ChatViewModel with parameters *********/
class ChatViewModelFactory(private val chatPartnerId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatPartnerId) as T
        }
        throw IllegalArgumentException("ERROR CHAT VIEWMODEL")
    }
}
