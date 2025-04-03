package com.moviles.clothingapp.weatherBanner.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.post.PostViewModel
import com.moviles.clothingapp.post.ui.PostItem
import com.moviles.clothingapp.ui.utils.NetworkHelper.isInternetAvailable
import com.moviles.clothingapp.ui.utils.NoInternetMessage


/* Screen to see after clicking the home's promoBanner:
*   - Features all products for a given category based on the user's weather.
*   - The retrieved data needs to have a 'category' variable.
*   - TODO: link each post to its detailed page.
*/
@Composable
fun WeatherCategoryScreen(categoryId: String, navController: NavController, viewModel: PostViewModel) {
    val trace: Trace = FirebasePerformance.getInstance().newTrace("WeatherClothesScreen_trace")
    val context = LocalContext.current
    trace.start()
    /* Launch the query for category of weather */
    LaunchedEffect(categoryId) {
        viewModel.fetchPostsByCategory(categoryId)
    }

    val posts by viewModel.posts.collectAsState()

    LaunchedEffect(posts) {
        if (posts.isNotEmpty()) {
            trace.stop()
        }
    }



    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
            }
            Text(
                text = getCategoryTitle(categoryId),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        if (!isInternetAvailable(context)) {
            Log.d("Status Internet", isInternetAvailable(context).toString())
            NoInternetMessage()
        }

        /* Display loading or posts */
        if (posts.isEmpty()) {
            Box( // to center it
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DarkGreen)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(8.dp)
            ) {
                items(posts) { post ->
                    PostItem(post) {
                        navController.navigate("detailedPost/${post.id}")
                    }
                }
            }
        }
    }
}



/* Auxiliary function to get readable category titles */
fun getCategoryTitle(categoryId: String): String {
    return when (categoryId) {
        "Calor" -> "Ropa ligera para el calor"
        "Frio" -> "Ropa abrigada"
        "Lluvia" -> "Especial para Días lluviosos"
        "Nublado" -> "Para días nublados "
        "Oferta" -> "Mejores precios"
        else -> "Productos"
    }
}
