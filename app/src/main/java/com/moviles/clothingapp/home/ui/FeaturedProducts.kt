package com.moviles.clothingapp.home.ui


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.moviles.clothingapp.home.HomeViewModel
import com.moviles.clothingapp.post.ui.PostItem
import com.moviles.clothingapp.ui.utils.NetworkHelper.isInternetAvailable
import com.moviles.clothingapp.ui.utils.NoInternetMessage
import com.moviles.clothingapp.ui.utils.dmSansFamily


/* SECCION DESTACADOS */
@Composable
fun FeaturedProducts(navController: NavController, viewModel: HomeViewModel) {
    val allProducts by viewModel.postData.observeAsState(emptyList())
    val products = allProducts.takeLast(6)
    val context = LocalContext.current

    LaunchedEffect(allProducts.size) {
        Log.d("FeaturedProducts", "Products updated, size: ${allProducts.size}")
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

        if (!isInternetAvailable(context)) { //TODO: Add that also IF there is nothing in CACHE.
            Log.d("Status Internet", isInternetAvailable(context).toString())
            NoInternetMessage()
        }

        // Wrap in a Box with height to avoid infinite scrolling error
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Set finite height
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize()
            ) {
                items(products) { product ->
                    PostItem(product) {
                        navController.navigate("detailedPost/${product.id}")
                    }
                }
            }
        }
    }
}
