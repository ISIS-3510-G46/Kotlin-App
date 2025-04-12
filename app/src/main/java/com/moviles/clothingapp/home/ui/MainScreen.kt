package com.moviles.clothingapp.home.ui


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.moviles.clothingapp.R
import com.moviles.clothingapp.home.HomeViewModel
import com.moviles.clothingapp.ui.utils.BottomNavigationBar
import com.moviles.clothingapp.ui.utils.QuickActions
import com.moviles.clothingapp.ui.utils.SearchBar
import com.moviles.clothingapp.weatherBanner.WeatherViewModel
import com.moviles.clothingapp.weatherBanner.ui.PromoBanner


@Composable
fun MainScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val banner = weatherViewModel.bannerType.observeAsState()
    val searchText = remember { mutableStateOf("") } // Store search text
    Log.d("MainScreen", "Observed banner value: ${banner.value}")

    val trace: Trace = remember { FirebasePerformance.getInstance().newTrace("MainScreen_Loading") }
    LaunchedEffect(Unit) {
        trace.start() // Start tracing when screen loads
        if (banner.value != null){
            trace.stop()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            item {
                SearchBar(
                    searchText = searchText.value,
                    onSearchTextChange = { newText -> searchText.value = newText },
                    onSearchSubmit = {
                        navController.navigate("discover/${searchText.value}")
                    }
                )
                Spacer(Modifier.height(8.dp))
            }

            //item { QuickActions() } d
            item { PromoBanner(bannerType = banner.value, navController = navController) }
            item { CategorySection(categoryList = categoryList, navController = navController) }
            item { FeaturedProducts(navController = navController, homeViewModel) }

        }
    }
}
