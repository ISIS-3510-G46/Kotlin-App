package com.moviles.clothingapp.cart.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.moviles.clothingapp.R
import com.moviles.clothingapp.BuildConfig
import com.moviles.clothingapp.cart.CartViewModel
import com.moviles.clothingapp.cart.data.CartItemEntity
import com.moviles.clothingapp.post.data.PostData
import com.moviles.clothingapp.ui.utils.BottomNavigationBar
import com.moviles.clothingapp.ui.utils.CoilProvider
import com.moviles.clothingapp.ui.utils.DarkGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val context = LocalContext.current
    val scope = CoroutineScope(
        Dispatchers.Main + Job()
    )
    Log.d("CART", "${cartViewModel.cartItems}")

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Cart header
            Text(
                text = "Mi Carrito",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (cartItems.isEmpty()) {
                // Empty cart view
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
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Carrito vacío",
                            modifier = Modifier.size(100.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tu carrito está vacío",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Explora nuestra tienda y añade productos a tu carrito",
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
            } else {
                // Cart items list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(cartItems) { cartItem ->

                        //Convert to PostData
                        val post = cartItem

                        CartItemCard(
                            cartItem = post,
                            onRemove = {
                                val productId = post.id
                                try {
                                    if (productId != null) {
                                        scope.launch{cartViewModel.removeFromCart(context, productId.toString())}
                                    }
                                } catch (e: Exception) {
                                    Log.e("CartScreen", "Failed to del item: $productId", e)
                                }
                            },
                            navController
                        )
                    }
                }

                Button(
                    onClick = { /* Navigate to payment */ },
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Proceder al pago")

                }
            }
        }
    }
}


@Composable
fun CartItemCard(
    cartItem: CartItemEntity,
    onRemove: () -> Unit,
    navController: NavController
) {

    val context = LocalContext.current
    val imageLoader = remember(context) {
        CoilProvider.get(context)
    }
    val product =  PostData(
        id = cartItem.postId,
        name = cartItem.name,
        price = cartItem.price,
        size = cartItem.size,
        brand = cartItem.brand,
        image = cartItem.imageUrl,
        category = cartItem.category,
        color = cartItem.color,
        group = cartItem.group,
        thumbnail = cartItem.thumbnail
    )
    val bucketId = BuildConfig.BUCKET_ID
    val projectId = "moviles"
    val imageUrl = if (product.image.startsWith("http")) {
        product.image
    } else {
        "https://cloud.appwrite.io/v1/storage/buckets/$bucketId/files/${product.image}/view?project=$projectId"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = { navController.navigate("detailedPost/${product.id}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            AsyncImage(
                model = imageUrl,
                imageLoader = imageLoader,
                placeholder = painterResource(R.drawable.placeholder),
                error       = painterResource(R.drawable.image_error),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )


            Spacer(modifier = Modifier.width(16.dp))

            // Product details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$ ${product.price} COP",
                    fontSize = 14.sp,
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Talla: ${product.size}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Rounded.RemoveCircle,
                    contentDescription = "Remove",
                    tint = Color.Red
                )
            }
        }
    }
}