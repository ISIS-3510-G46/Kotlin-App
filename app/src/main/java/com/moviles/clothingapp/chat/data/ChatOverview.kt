package com.moviles.clothingapp.chat.data

import com.google.firebase.Timestamp

data class ChatOverview(
    val chatPartnerId: String = "",
    val lastMessage: String = "",
    val timestamp: Timestamp? = null
)