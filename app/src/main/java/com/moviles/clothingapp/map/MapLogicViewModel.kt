package com.moviles.clothingapp.map

import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.viewModelScope
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.moviles.clothingapp.map.data.Shop
import com.moviles.clothingapp.map.data.ShopsData
import com.moviles.clothingapp.ui.utils.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MapLogicViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    /* User location latitude and longitude */
    private val _userLocation = MutableLiveData<LatLng>()
    val userLocation: LiveData<LatLng> get() = _userLocation

    /* Camera position (initial and to change) */
    val cameraPositionState = CameraPositionState()

    /* Default location - Mario Laserna Building */
    val defaultLocation = LatLng(4.602904573111566, -74.06503868957138)
    private val appContext = getApplication<Application>().applicationContext


    /* Shop fetching */
    val shopLocations: LiveData<List<Shop>> = MutableLiveData(ShopsData.shopLocations)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading



    /* Load User location */
    init {
        getUserLocation()
        _isLoading.value = false
    }

    init {
        Log.d("ViewModelLifecycle", "${this.javaClass.simpleName} created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("ViewModelLifecycle", "${this.javaClass.simpleName} destroyed")
    }


    /* Get user location or put it in default location */
    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hasInternet = NetworkHelper.isInternetAvailable(appContext)
                val location = if (hasInternet) {
                    fusedLocationClient.lastLocation.await()
                } else {
                    null
                }
                val userLatLng = location?.let {
                    LatLng(it.latitude, it.longitude)
                } ?: defaultLocation
                val zoom = if (location != null) 15f else 12f

                withContext(Dispatchers.Main) {
                    _userLocation.value = userLatLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLatLng, zoom)
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _userLocation.value = defaultLocation
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(defaultLocation, 12f)
                }
            }
        }
    }

    /* Function to focus the map view if a store from list is selected */
    fun focusOnLocation(location: LatLng) {
        viewModelScope.launch(Dispatchers.Main) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(location)
                        .zoom(17f)
                        .build()
                ),
                durationMs = 1000
            )
        }
    }
}
