package com.moviles.clothingapp.chat.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.clothingapp.chat.ChatViewModel
import com.moviles.clothingapp.chat.ChatViewModelFactory
import com.moviles.clothingapp.chat.data.ChatMessage
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.NetworkHelper.isInternetAvailable
import com.moviles.clothingapp.ui.utils.NoInternetMessage
import com.moviles.clothingapp.ui.utils.dmSansFamily
import com.moviles.clothingapp.ui.utils.figtreeFamily
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentUserId: String,
    chatPartnerId: String,
    productId: Int = -1,
    onBackClick: () -> Unit
) {
    /* Create ViewModel using the factory:
    *   - Serves as a instance of the viewModel but allowing to pass it specific parameters.
    */
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(chatPartnerId)
    )

    /* Observe states from ViewModel */
    val messages by viewModel.messages.collectAsState()
    val productName by viewModel.productName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    /* Initialize conversation with product ID */
    LaunchedEffect(chatPartnerId, productId) {
        viewModel.loadMessages()

        if (productId > 0) { /* If not default value, load product details */
            viewModel.loadProductName(productId)
            viewModel.initializeConversation(productId)
        } else {
            /* Try to load from chat metadata */
            viewModel.loadConversationName()
        }
    }

    /* UI message to send */
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        "Chat con usuario",
                        fontFamily = figtreeFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                    )
                    productName?.let { name ->
                        Text(
                            text = "Del producto: $name",
                            fontFamily = figtreeFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            },

            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        )

        if (!isInternetAvailable(context)) {
            Log.d("Status Internet", isInternetAvailable(context).toString())
            NoInternetMessage()
        } else {

            /* Loading indicator */
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            /* Messages (from current user and to other user) */
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true // Bottom to top messages (like ig)
            ) {
                items(messages) { message ->
                    val isFromCurrentUser = message.senderId == currentUserId
                    MessageBubble(
                        message = message,
                        isFromCurrentUser = isFromCurrentUser
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            /* Input message box */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = {
                        Text(
                            "Escribe un mensaje..",
                            color = Color.LightGray,
                            fontFamily = dmSansFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkGreen,
                        unfocusedContainerColor = DarkGreen,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(
                        fontFamily = dmSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )
                )

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = DarkGreen,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "Send message",
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}


/* Each message bubble */
@Composable
fun MessageBubble(
    message: ChatMessage,
    isFromCurrentUser: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isFromCurrentUser)
                        DarkGreen
                    else
                        Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isFromCurrentUser)
                    Color.White
                else
                    Color.DarkGray
            )
        }

        Text(
            text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.timestamp),
            fontFamily = dmSansFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}