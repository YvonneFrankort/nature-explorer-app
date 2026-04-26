package com.example.naturegame.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.data.repository.NatureSpotRepository
import com.example.naturegame.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject
import com.example.naturegame.data.remote.firebase.AuthManager

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: NatureSpotRepository,
    private val locationManager: LocationManager,
    private val authManager: AuthManager
) : ViewModel() {

    val routePoints: StateFlow<List<GeoPoint>> = locationManager.routePoints
    val currentLocation: StateFlow<Location?> = locationManager.currentLocation

    private val _natureSpots = MutableStateFlow<List<NatureSpot>>(emptyList())
    val natureSpots: StateFlow<List<NatureSpot>> = _natureSpots.asStateFlow()

    init {
        viewModelScope.launch {
            while (authManager.currentUserId == null) {
                kotlinx.coroutines.delay(50)
            }
            loadNatureSpots()
        }
    }

    fun startTracking() = locationManager.startTracking()
    fun stopTracking() = locationManager.stopTracking()
    fun resetRoute() = locationManager.resetRoute()

    private fun loadNatureSpots() {
        viewModelScope.launch {
            repository.allSpots.collectLatest { spots ->
                _natureSpots.value = spots
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopTracking()
    }
}

fun Long.toFormattedDate(): String {
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(this))
}
