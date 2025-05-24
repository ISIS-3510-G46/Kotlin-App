package com.moviles.clothingapp.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.core.content.edit
import androidx.core.graphics.scale

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // LiveData for UI state
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _profileImagePath = MutableLiveData<String?>()
    val profileImagePath: LiveData<String?> = _profileImagePath

    private val _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> = _userLocation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = prefs.getString("user_name", "Pepito Perez") ?: "Pepito Perez"
            val imagePath = prefs.getString("profile_image_path", null)
            val location = prefs.getString("user_location", "Ubicación no configurada")
                ?: "Ubicación no configurada"

            withContext(Dispatchers.Main) {
                _userName.value = name
                _profileImagePath.value = imagePath
                _userLocation.value = location
            }
        }
    }

    fun closeSession() {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit {
                remove("user_name")
                remove("profile_image_path")
                remove("user_location")
                remove("user_latitude")
            }
        }}


    fun updateUserName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.edit { putString("user_name", name) }
            withContext(Dispatchers.Main) {
                _userName.value = name
            }
        }
    }

    fun saveProfileImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Compress image if too large
                val scaledBitmap = scaleBitmapIfNeeded(bitmap)

                // Save to internal storage
                val filename = "profile_${System.currentTimeMillis()}.jpg"
                val file = File(context.filesDir, filename)

                FileOutputStream(file).use { out ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }

                // Delete old profile image if exists
                _profileImagePath.value?.let { oldPath ->
                    File(oldPath).delete()
                }

                // Save new path to SharedPreferences
                prefs.edit().putString("profile_image_path", file.absolutePath).apply()

                withContext(Dispatchers.Main) {
                    _profileImagePath.value = file.absolutePath
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error al guardar la imagen: ${e.message}"
                    _isLoading.value = false
                }
            }
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val maxSize = 1024 // max width/height in pixels

        return if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val scale = minOf(
                maxSize.toFloat() / bitmap.width,
                maxSize.toFloat() / bitmap.height
            )
            val newWidth = (bitmap.width * scale).toInt()
            val newHeight = (bitmap.height * scale).toInt()

            bitmap.scale(newWidth, newHeight)
        } else {
            bitmap
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("DefaultLocale")
    fun saveUserLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            // Here you could reverse geocode to get address
            val locationString = "Lat: ${String.format("%.4f", latitude)}, " +
                    "Lon: ${String.format("%.4f", longitude)}"

            prefs.edit { putString("user_location", locationString) }
            prefs.edit {
                putFloat("user_latitude", latitude.toFloat())
                    .putFloat("user_longitude", longitude.toFloat())
            }

            withContext(Dispatchers.Main) {
                _userLocation.value = locationString
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}