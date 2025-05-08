package com.moviles.clothingapp.chat.data

data class ChatMessage(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)