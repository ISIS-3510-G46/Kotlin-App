package com.moviles.clothingapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.moviles.clothingapp.navigation.AppNavigation
import com.moviles.clothingapp.login.LoginViewModel
import com.moviles.clothingapp.login.ResetPasswordViewModel
import com.moviles.clothingapp.weatherBanner.WeatherViewModel
import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.perf.FirebasePerformance
import com.moviles.clothingapp.cart.CartViewModel
import com.moviles.clothingapp.home.data.cache.RecentProductsCache
import com.moviles.clothingapp.favoritePosts.FavoritesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/* The mainActivity initializes all the app */
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth // Initializes when onCreate
    private lateinit var firebaseAnalytics: FirebaseAnalytics // Initializes when onCreate
    private val cartViewModel: CartViewModel by viewModels()
    private val loginViewModel by lazy { LoginViewModel(Firebase.auth) }
    private val resetPasswordViewModel by lazy { ResetPasswordViewModel(Firebase.auth) }
    private val weatherViewModel by lazy { WeatherViewModel(this, false) }
    private val favoritesViewModel: FavoritesViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Initialize Firebase services */
        auth = Firebase.auth
        firebaseAnalytics = Firebase.analytics

        setContent {
            val navController = rememberNavController()
            AppNavigation(navController, loginViewModel, resetPasswordViewModel, weatherViewModel, cartViewModel, favoritesViewModel)
        }

        /* Request location permissions */
        // requestLocationPermissions()
        Handler(Looper.getMainLooper()).postDelayed({
            requestLocationPermissions()
        }, 1000)

        /* Log device information and metrics for firebase */
        logDeviceInfo()

    }


    /* Auxiliary function to record device info in firebase */
    private fun logDeviceInfo() {
        lifecycleScope.launch {
            delay(3000) // wait 3 seconds before rendering
            Log.d(
                "FirebasePerf",
                "Firebase Performance Monitoring initialized: ${FirebasePerformance.getInstance()}"
            )
            val deviceInfo = bundleOf(
                "device_model" to Build.MODEL,
                "device_brand" to Build.BRAND,
                "os_version" to Build.VERSION.RELEASE
            )

            withContext(Dispatchers.IO) { // adding dispatcher here to avoid crashing
                firebaseAnalytics.logEvent("device_info", deviceInfo)
                Log.d("DEVICES", deviceInfo.toString())
            }
        }
    }

    /* Auxiliary functions to request location permissions and initialize weatherViewModel to fetch data */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val permissionGranted = permissions.entries.all { it.value }
        weatherViewModel.updateLocationPermission(permissionGranted)
    }

    private fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                weatherViewModel.updateLocationPermission(true)
            }
            else -> {
                // Request permissions
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}