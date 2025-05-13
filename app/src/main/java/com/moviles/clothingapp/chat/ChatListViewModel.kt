package com.moviles.clothingapp.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.moviles.clothingapp.chat.data.ChatOverview
import com.moviles.clothingapp.chat.data.ChatOverviewWithProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class ChatListViewModel : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatOverviewWithProduct>>(emptyList())
    val chatList: StateFlow<List<ChatOverviewWithProduct>> = _chatList

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid

    init {
        if (currentUserId != null) { // check if user has id (seed for posts doesn't have ids)
            loadChatList(currentUserId = currentUserId)
        }
    }


    /* Fetch chats from firebase store */
    fun loadChatList(currentUserId: String) {
        firestore.collection("users")
            .document(currentUserId)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    viewModelScope.launch {
                        try {
                            val chatOverviews = snapshot.toObjects(ChatOverview::class.java)
                            _chatList.value = chatOverviews.map { ChatOverviewWithProduct(it, null) }
                        } catch (e: Exception) {
                            Log.e("ERROR fetching chat list", e.toString())
                        }
                    }
                } else {
                    _chatList.value = emptyList()
                }
            }
    }
}