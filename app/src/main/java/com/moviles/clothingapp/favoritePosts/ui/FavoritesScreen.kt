package com.moviles.clothingapp.favoritePosts.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moviles.clothingapp.favoritePosts.FavoritesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.post.ui.PostItem
import com.moviles.clothingapp.ui.utils.BottomNavigationBar
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.NetworkHelper.isInternetAvailable
import com.moviles.clothingapp.ui.utils.NoInternetMessage
import com.moviles.clothingapp.ui.utils.smallNoInternetMessage


@Composable
fun FavoritesScreen(navController: NavController ,favoritesViewModel: FavoritesViewModel) {
    val favorites by favoritesViewModel.favorites.collectAsState()
    val context = LocalContext.current
    Log.d("Favorite", favorites.toString())



    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.HeartBroken,
                    contentDescription = "No hay favoritos",
                    modifier = Modifier.size(100.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No tienes productos favoritos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explora nuestra tienda y añade productos de tu interés.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text("Explorar productos")
                }
            }
        }
    }


    else {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tus favoritos",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                if (!isInternetAvailable(context)) {
                    Log.d("Status Internet", isInternetAvailable(context).toString())
                    smallNoInternetMessage()
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(favorites) { favoriteEntity ->
                        // Convert FavoriteEntity to PostData
                        val post = PostData(
                            id = favoriteEntity.postId,
                            name = favoriteEntity.name,
                            price = favoriteEntity.price,
                            size = favoriteEntity.size,
                            brand = favoriteEntity.brand,
                            image = favoriteEntity.imageUrl,
                            category = favoriteEntity.category,
                            color = favoriteEntity.color,
                            group = favoriteEntity.group,
                            thumbnail = favoriteEntity.thumbnail
                        )

                        PostItem(
                            post = post
                        ) { navController.navigate("detailedPost/${post.id}") }
                    }
                }
            }
        }
    }
}
