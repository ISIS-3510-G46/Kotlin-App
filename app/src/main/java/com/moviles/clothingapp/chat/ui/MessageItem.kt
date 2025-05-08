package com.moviles.clothingapp.chat.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moviles.clothingapp.chat.data.ChatMessage

@Composable
fun MessageBubble(message: ChatMessage, isMe: Boolean) {
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMe) Color(0xFFDCF8C6) else Color.LightGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = message.message,
                modifier = Modifier.padding(10.dp),
                color = Color.Black
            )
        }
    }
}
