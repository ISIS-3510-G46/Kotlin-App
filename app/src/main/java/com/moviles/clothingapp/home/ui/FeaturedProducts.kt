package com.moviles.clothingapp.home.ui


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.clothingapp.home.data.cache.RecentProductsCache
import com.moviles.clothingapp.post.ui.PostItem
import com.moviles.clothingapp.ui.utils.NoInternetMessage
import com.moviles.clothingapp.ui.utils.dmSansFamily


/* SECCION DESTACADOS */
@Composable
fun FeaturedProducts(navController: NavController) {
    val featured by remember { RecentProductsCache.flow }.collectAsState(initial = emptyList())


    LaunchedEffect(Unit) {
        RecentProductsCache.load()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Recién llegados",
                fontSize = 20.sp,
                fontFamily = dmSansFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 5.dp)
            )
        }

        if (featured.isEmpty()) {
            NoInternetMessage()}

        // Wrap in a Box with height to avoid infinite scrolling error
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Set finite height
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(featured) { product ->
                    PostItem(product) {
                        navController.navigate("detailedPost/${product.id}")
                    }
                }
            }
        }
    }
}
