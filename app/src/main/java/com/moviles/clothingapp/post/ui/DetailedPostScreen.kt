package com.moviles.clothingapp.post.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.moviles.clothingapp.BuildConfig
import com.moviles.clothingapp.R
import com.moviles.clothingapp.cart.CartViewModel
import com.moviles.clothingapp.favoritePosts.FavoritesViewModel
import com.moviles.clothingapp.post.PostViewModel
import com.moviles.clothingapp.ui.utils.CoilProvider
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.Red

@Composable
fun DetailedPostScreen(
    productId: Int,
    viewModel: PostViewModel = viewModel(),
    favoritesViewModel: FavoritesViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val product by viewModel.post.collectAsStateWithLifecycle()
    val isFavorite = remember(product) {
        mutableStateOf(false)
    }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        viewModel.fetchPostById(productId)
    }

    LaunchedEffect(product) {
        product?.id?.let { postId ->
            val isProductFavorite = favoritesViewModel.isFavorite(context, postId)
            isFavorite.value = isProductFavorite
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        product != null -> {
            val imageLoader = remember(context) {
                CoilProvider.get(context)
            }
            val bucketId = BuildConfig.BUCKET_ID
            val projectId = "moviles"
            val imageUrl = remember(product!!.image) {
                if (product!!.image.startsWith("http")) {
                    product!!.image
                } else {
                    "https://cloud.appwrite.io/v1/storage/buckets/$bucketId/files/${product!!.image}/view?project=$projectId"
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp)
                    .background(Color.White)
            ) {
                // Product Image with navigation and favorite buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product!!.name,
                        imageLoader = imageLoader,
                        placeholder = painterResource(R.drawable.placeholder),
                        error       = painterResource(R.drawable.image_error),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )

                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    IconButton(
                        onClick = {
                            product?.id.let { postId ->
                                isFavorite.value = !isFavorite.value
                                favoritesViewModel.newFavorite(context, product!!)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite.value)
                                Icons.Rounded.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Black
                        )
                    }

                }


                // Product details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Product title
                    Text(
                        text = product!!.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = product!!.brand,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Product attributes section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        // First row - Size and Category
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Size attribute card
                            AttributeCard(
                                icon = Icons.Rounded.Straighten,
                                tint = DarkGreen,
                                label = "Tamaño",
                                value = product!!.size,
                                modifier = Modifier.weight(1f)
                            )

                            // Category attribute card
                            AttributeCard(
                                icon = Icons.Rounded.WbSunny,
                                tint = DarkGreen,
                                label = "Categoría",
                                value = product!!.category,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Second row - Group and Color
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Group (gender) attribute card
                            AttributeCard(
                                icon = Icons.Rounded.Person,
                                tint = DarkGreen,
                                label = "Para",
                                value = product!!.group,
                                modifier = Modifier.weight(1f)
                            )

                            // Color attribute card
                            AttributeCard(
                                icon = Icons.Rounded.Palette,
                                tint = DarkGreen,
                                label = "Color",
                                value = product!!.color,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }




                    Spacer(modifier = Modifier.weight(1f))

                    // Add to cart button
                    Button(
                        onClick = {
                            product?.let {
                                Log.d("DetailedPostScreen", "Adding product to cart: ${it.name}, ID: ${it.id}, Price: ${it.price}")
                                cartViewModel.addToCart(it)
                                Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                                onNavigateToCart()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Agregar al carrito | " + "$${product!!.price}",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error cargando el producto, verifica tu conexion a internet.",
                            color = Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Go Back button
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }


// Helper composable for attribute cards
@Composable
private fun AttributeCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tint: Color
) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = "",
                tint = tint,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Label and value
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}