package com.moviles.clothingapp.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val RECOMMENDATION_WORK = "weather_recommendation_work"

    /**
     * Programa una notificación de producto recomendado según el clima actual
     */
    fun scheduleRecommendation(context: Context, delayMinutes: Long = 10) {
        val recommendationWork = OneTimeWorkRequestBuilder<WeatherProductNotificationWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            RECOMMENDATION_WORK,
            ExistingWorkPolicy.REPLACE,
            recommendationWork
        )
    }

    /**
     * Cancela la notificación programada
     */
    fun cancelRecommendation(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(RECOMMENDATION_WORK)
    }
}