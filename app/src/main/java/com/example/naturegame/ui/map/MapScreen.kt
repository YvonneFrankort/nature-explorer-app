package com.example.naturegame.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naturegame.viewmodel.CameraViewModel
import com.example.naturegame.viewmodel.MapViewModel
import com.example.naturegame.viewmodel.WalkViewModel
import com.example.naturegame.viewmodel.ProfileViewModel
import com.example.naturegame.viewmodel.toFormattedDate
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturegame.utils.getCategoryColorHex
import com.example.naturegame.utils.getTintedDefaultMarker
import com.example.naturegame.utils.scaleDrawable
import com.example.naturegame.data.local.entity.NatureSpot

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
    walkViewModel: WalkViewModel = viewModel(),
    cameraViewModel: CameraViewModel = viewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Permissions
    LocationAndActivityPermissions(onAllGranted = {})

    // State
    val isWalking by walkViewModel.isWalking.collectAsState()
    val routePoints by mapViewModel.routePoints.collectAsState()
    val currentLocation by mapViewModel.currentLocation.collectAsState()
    val natureSpots by mapViewModel.natureSpots.collectAsState()

    // Start/stop GPS tracking
    LaunchedEffect(isWalking) {
        if (isWalking) {
            mapViewModel.resetRoute()
            mapViewModel.startTracking()
        } else {
            mapViewModel.stopTracking()
        }
    }

    val defaultPosition = GeoPoint(65.0121, 25.4651)

    // Required for osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {

            val mapViewState = remember { MapView(context) }

            var selectedSpot by remember { mutableStateOf<NatureSpot?>(null) }

            LaunchedEffect(natureSpots) {
                natureSpots.lastOrNull()?.let { spot ->
                    mapViewState.controller.animateTo(
                        GeoPoint(spot.latitude, spot.longitude),
                        17.0,
                        1000L
                    )
                }
            }

            // Initialize MapView once
            DisposableEffect(Unit) {
                mapViewState.setTileSource(TileSourceFactory.MAPNIK)
                mapViewState.setMultiTouchControls(true)
                mapViewState.controller.setZoom(15.0)
                mapViewState.controller.setCenter(
                    currentLocation?.let { GeoPoint(it.latitude, it.longitude) }
                        ?: defaultPosition
                )
                onDispose { mapViewState.onDetach() }
            }

            AndroidView(
                factory = { mapViewState },
                modifier = Modifier.fillMaxSize(),
                update = { mapView ->

                    mapView.overlays.clear()

                    // Draw route polyline
                    if (routePoints.size >= 2) {
                        val polyline = Polyline().apply {
                            setPoints(routePoints)
                            outlinePaint.color = 0xFF2E7D32.toInt()
                            outlinePaint.strokeWidth = 8f
                        }
                        mapView.overlays.add(polyline)
                    }

                    // Draw NatureSpot markers
                    natureSpots.forEach { spot ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(spot.latitude, spot.longitude)
                            title = spot.plantLabel ?: spot.name
                            snippet = spot.timestamp.toFormattedDate()

                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            val colorHex = getCategoryColorHex(spot.plantLabel ?: "unknown")
                            val tintedIcon = getTintedDefaultMarker(context, colorHex)

                            if (tintedIcon != null) {
                                val zoom = mapView.zoomLevelDouble.toFloat()
                                val scale = (zoom / 15f).coerceIn(1f, 3f)   // zoom-based scaling
                                icon = scaleDrawable(tintedIcon, scale)
                            }
                            setOnMarkerClickListener { _, _ ->
                                selectedSpot = spot
                                true
                            }
                        }

                        mapView.overlays.add(marker)
                    }

                    // Update CameraViewModel with GPS coordinates
                    currentLocation?.let { loc ->
                        cameraViewModel.currentLatitude = loc.latitude
                        cameraViewModel.currentLongitude = loc.longitude

                        mapView.controller.animateTo(
                            GeoPoint(loc.latitude, loc.longitude),
                            16.0,
                            1000L
                        )
                    }

                    mapView.invalidate()
                }
            )

            if (selectedSpot != null) {
                NatureSpotPopup(
                    spot = selectedSpot!!,
                    onDismiss = { selectedSpot = null }
                )
            }
        }

        WalkStatsCard(
            viewModel = walkViewModel,
            profileViewModel = profileViewModel
        )
    }
}
