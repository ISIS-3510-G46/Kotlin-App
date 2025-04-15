package com.moviles.clothingapp.favoritePosts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.ui.utils.figtreeFamily

@Composable
fun FavoriteBrandBanner(
    post: PostData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🔥",
                fontFamily = figtreeFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 8.dp)
            )

            Text(
                text = "Nuevo producto de tu marca favorita: ${post.brand}!",
                fontFamily = figtreeFamily,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}