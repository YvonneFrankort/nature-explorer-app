package com.example.naturegame.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

class LocationManager(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _routePoints = MutableStateFlow<List<GeoPoint>>(emptyList())
    val routePoints: StateFlow<List<GeoPoint>> = _routePoints.asStateFlow()

    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startTracking() {

        if (callback != null) {
            return
        }

        val request: LocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L // update every 2 seconds
        )
            .setMinUpdateDistanceMeters(1f)
            .build()

        callback = (object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return

                _currentLocation.value = loc

                val point = GeoPoint(loc.latitude, loc.longitude)
                _routePoints.value = _routePoints.value + point
            }
        }) as LocationCallback


        println("DEBUG: Requesting location updates…")

        fusedClient.requestLocationUpdates(
            request,
            callback!!,
            context.mainLooper
        )
    }

    fun stopTracking() {
        println("DEBUG: stopTracking() CALLED")
        callback?.let { fusedClient.removeLocationUpdates(it) }
        callback = null
    }

    fun resetRoute() {
        println("DEBUG: resetRoute() CALLED")
        _routePoints.value = emptyList()
    }

    fun calculateTotalDistance(): Float {
        val points = _routePoints.value
        if (points.size < 2) return 0f

        var total = 0f
        for (i in 0 until points.size - 1) {
            val results = FloatArray(1)
            Location.distanceBetween(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude,
                results
            )
            total += results[0]
        }
        return total
    }
}
