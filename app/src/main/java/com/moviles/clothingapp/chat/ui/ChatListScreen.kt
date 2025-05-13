package com.moviles.clothingapp.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.clothingapp.chat.ChatListViewModel
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.dmSansFamily
import com.moviles.clothingapp.ui.utils.figtreeFamily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    currentUserId: String,
    onChatClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: ChatListViewModel = viewModel()
    val chatList by viewModel.chatList.collectAsState()


    LaunchedEffect(true) {
        viewModel.loadChatList(currentUserId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mensajes",
                        fontFamily = figtreeFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (chatList.isEmpty()) {
            EmptyChatList(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(chatList) { chat ->
                    ChatListItem(
                        lastMessage = chat.chatOverview.lastMessage,
                        timestamp = chat.chatOverview.timestamp,
                        onClick = {
                            onChatClick(chat.chatOverview.chatPartnerId)
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ChatBubbleOutline,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = DarkGreen
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No tienes mensajes",
            fontFamily = figtreeFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tus conversaciones aparecerán aquí, ingresa a alguna publicación para iniciar una conversación.",
            fontFamily = figtreeFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp).wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ChatListItem(
    lastMessage: String,
    timestamp: Date = Date(),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        /* Profile circle for each chat */
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Continua tu conversación..",
                    fontFamily = dmSansFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    // todo: for later naming
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = formatChatTime(timestamp),
                    fontFamily = dmSansFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,

                )
            }

            Text(
                text = lastMessage,
                fontFamily = dmSansFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* Helper function to format chat times and hours */
fun formatChatTime(date: Date): String {
    val colombiaTimeZone = TimeZone.getTimeZone("America/Bogota")
    val now = Calendar.getInstance(colombiaTimeZone)
    val messageTime = Calendar.getInstance(colombiaTimeZone).apply { time = date }

    return when {
        /* Today */
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR) -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
                timeZone = colombiaTimeZone
            }.format(date)
        }
        /* Yesterday */
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - messageTime.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "Ayer"
        }
        /* This week */
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                now.get(Calendar.WEEK_OF_YEAR) == messageTime.get(Calendar.WEEK_OF_YEAR) -> {
            SimpleDateFormat("EEEE", Locale("es")).apply {
                timeZone = colombiaTimeZone
            }.format(date)
        }
        /* This year */
        now.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) -> {
            SimpleDateFormat("d MMM", Locale("es")).apply {
                timeZone = colombiaTimeZone
            }.format(date)
        }
        /* Older */
        else -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).apply {
                timeZone = colombiaTimeZone
            }.format(date)
        }
    }
}

