package com.moviles.clothingapp.chat.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.clothingapp.chat.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    currentUserId: String,
    onChatClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    val viewModel: ChatListViewModel = viewModel()
    val chatList by viewModel.chatList.collectAsState()

    LaunchedEffect(true) {
        viewModel.loadChatList(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chats") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(chatList) { chat ->
                ChatListItem(chatPartnerId = chat.chatPartnerId, lastMessage = chat.lastMessage) {
                    onChatClick(chat.chatPartnerId)
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chatPartnerId: String,
    lastMessage: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = chatPartnerId, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = lastMessage, color = Color.Gray)
    }
}
