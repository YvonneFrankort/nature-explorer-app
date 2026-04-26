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

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    fun pause() { _isPaused.value = true }
    fun resume() { _isPaused.value = false }

    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startTracking() {

        if (callback != null) return

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            500L
        )
            .setMinUpdateIntervalMillis(250L)
            .setMinUpdateDistanceMeters(0f)
            .build()

        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return

                _currentLocation.value = loc

                if (_isPaused.value) return

                val point = GeoPoint(loc.latitude, loc.longitude)
                val last = _routePoints.value.lastOrNull()

                if (last != null) {
                    val results = FloatArray(1)

                    Location.distanceBetween(
                        last.latitude, last.longitude,
                        point.latitude, point.longitude,
                        results
                    )

                    val distance = results[0]

                    if (distance < 1f) return

                    if (distance > 50f) return

                    if (distance < 5f) return
                }

                val finalPoint = if (last != null) {
                    GeoPoint(
                        (last.latitude + point.latitude) / 2,
                        (last.longitude + point.longitude) / 2
                    )
                } else {
                    point
                }

                _routePoints.value = _routePoints.value + finalPoint
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            callback!!,
            context.mainLooper
        )
    }

    fun stopTracking() {
        callback?.let { fusedClient.removeLocationUpdates(it) }
        callback = null
    }

    fun resetRoute() {
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
