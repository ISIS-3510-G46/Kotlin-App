package com.moviles.clothingapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moviles.clothingapp.MainActivity
import com.moviles.clothingapp.R
import com.moviles.clothingapp.post.data.PostRepository
import com.moviles.clothingapp.weatherBanner.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherProductNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "weather_product_recommendations"
        const val NOTIFICATION_ID = 1001
        private const val TAG = "WeatherNotification"
    }

    private val weatherRepository = WeatherRepository(context)
    private val postRepository = PostRepository()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 1. Verificar permisos de ubicación
            val hasLocationPermission = context.checkLocationPermission()

            // 2. Obtener datos del clima directamente del repositorio
            val weatherData = weatherRepository.getWeatherData(hasLocationPermission)
            Log.d(TAG, "Weather data: $weatherData")

            if (weatherData == null) {
                Log.e(TAG, "No weather data available")
                return@withContext Result.failure()
            }

            // 3. Determinar la categoría según el clima
            val categoryId = getCategoryFromWeatherData(weatherData)
            Log.d(TAG, "Selected category: $categoryId")

            // 4. Obtener productos de esa categoría
            val products = postRepository.fetchPostsByCategory(categoryId)
            if (products.isNullOrEmpty()) {
                Log.e(TAG, "No products found for category $categoryId")
                return@withContext Result.failure()
            }

            // 5. Mostrar notificación con el primer producto
            val product = products.first()
            val weatherMessage = getMessageForCategory(categoryId)

            showProductNotification(product.id, product.name, product.price, weatherMessage)

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in weather notification worker", e)
            Result.failure()
        }
    }

    private fun Context.checkLocationPermission(): Boolean {
        return android.content.pm.PackageManager.PERMISSION_GRANTED ==
                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                android.content.pm.PackageManager.PERMISSION_GRANTED ==
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun getCategoryFromWeatherData(weatherData: com.moviles.clothingapp.weatherBanner.data.WeatherResponse): String {
        val temperature = weatherData.main.temp
        val weatherDescription = weatherData.weather.firstOrNull()?.description?.lowercase() ?: ""

        return when {
            temperature > 25 -> "Calor"
            temperature < 15 -> "Frio"
            weatherDescription.contains("rain") ||
                    weatherDescription.contains("lluvia") ||
                    weatherDescription.contains("shower") -> "Lluvia"
            weatherDescription.contains("cloud") ||
                    weatherDescription.contains("nub") -> "Nublado"
            else -> "Oferta"
        }
    }

    private fun getMessageForCategory(category: String): String {
        return when (category) {
            "Calor" -> "¡Ideal para el calor de hoy!"
            "Frio" -> "¡Mantente abrigado con nuestra oferta especial!"
            "Lluvia" -> "¡No dejes que la lluvia te sorprenda!"
            "Nublado" -> "Perfecto para este día nublado"
            else -> "¡No te pierdas esta oferta especial!"
        }
    }

    private fun showProductNotification(
        productId: String?,
        title: String,
        price: String,
        weatherMessage: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificaciones (requerido para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recomendaciones según el clima",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ofertas personalizadas según el clima actual"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la pantalla de detalles del producto al tocar la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            // Usar la misma estructura de navegación que aparece en AppNavigation.kt
            putExtra("NAVIGATE_TO", "detailedPost/$productId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("¡Oferta especial para ti!")
            .setContentText("$title a solo $$price - $weatherMessage")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}