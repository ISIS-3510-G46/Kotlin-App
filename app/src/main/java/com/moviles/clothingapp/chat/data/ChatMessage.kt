package com.moviles.clothingapp.chat.data

import java.util.Date

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val timestamp: Date = Date()
)