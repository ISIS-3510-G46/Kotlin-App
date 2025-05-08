package com.moviles.clothingapp.chat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.clothingapp.chat.ChatViewModel


@Composable
fun ChatScreen(
    senderId: String,
    receiverId: String,
) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val messageText = remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            state = scrollState,
            reverseLayout = false
        ) {
            items(messages) { msg ->
                MessageBubble(message = msg, isMe = msg.senderId == senderId)
            }
        }

        // Input field + send button
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText.value,
                onValueChange = { messageText.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.sendMessage(senderId, receiverId, messageText.value)
                messageText.value = ""
            }) {
                Text("Send")
            }
        }
    }

    // Auto-scroll to bottom when messages update
    LaunchedEffect(messages.size) {
        scrollState.animateScrollToItem(messages.size)
    }
}
