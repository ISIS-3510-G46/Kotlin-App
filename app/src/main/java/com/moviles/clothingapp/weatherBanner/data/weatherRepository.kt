package com.moviles.clothingapp.weatherBanner.data

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.moviles.clothingapp.BuildConfig



/* Weather Repo: Manages the external API endpoint connection, request and parsing of response
*  for a user location. Also checks if the user's location is enabled.
*  Uses the same process featured for connecting to backend API.
 */
class WeatherRepository(private val context: Context) {
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/" //API for weather
    private val apiKey = BuildConfig.OPEN_WEATHER_API_KEY


    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService: WeatherApiService = retrofit.create(WeatherApiService::class.java)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getWeatherData(hasLocationPermission: Boolean = false): WeatherResponse? {
        val location = getCurrentLocation()
        Log.d("LOCATION", "" + location)
        if (location != null && hasLocationPermission) {
            return try {
                val response = apiService.getWeather(
                    location.latitude,
                    location.longitude,
                    apiKey,
                    "metric"
                )
                if (response.isSuccessful) {
                    Log.i(
                        "WeatherRepository",
                        "API Response: ${response.body()}"
                    )
                    return response.body()
                } else {
                    Log.e("WeatherRepository", "Response failed: ${response.body()}")
                    Log.e("WeatherRepository", "Response failed: ${apiKey}")
                    null
                }
            } catch (e: Exception) {
                Log.e("WeatherRepository", "Error: ${e.message}")
                null
            }
        }
        return null
    }

    suspend fun getCurrentLocation(): android.location.Location? {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("WeatherRepository", "Location permissions not granted")
            return null;
        }

        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                Log.d("WeatherRepository", "Location: $location")
                return location
            } else {
                Log.d("WeatherRepository", "Location not available")
                return null
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Error getting location: ${e.message}")
            return null
        }
    }


    interface WeatherApiService {
        @GET("weather") // Api endpoint
        suspend fun getWeather(
            @Query("lat") lat: Double,
            @Query("lon") lon: Double,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): Response<WeatherResponse>
    }
}