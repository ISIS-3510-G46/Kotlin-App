package com.moviles.clothingapp.chat.data
import java.util.Date

data class ChatOverview(
    val chatPartnerId: String = "",
    val lastMessage: String = "",
    val timestamp: Date = Date(),
    val productId: Int = -1
)


data class ChatOverviewWithProduct(
    val chatOverview: ChatOverview,
    val productName: String?
)