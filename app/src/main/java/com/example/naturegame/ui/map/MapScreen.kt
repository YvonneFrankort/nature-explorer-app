package com.example.naturegame.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.utils.getCategoryColorHex
import com.example.naturegame.utils.getTintedDefaultMarker
import com.example.naturegame.utils.scaleDrawable
import com.example.naturegame.viewmodel.MapViewModel
import com.example.naturegame.viewmodel.ProfileViewModel
import com.example.naturegame.viewmodel.WalkViewModel
import com.example.naturegame.viewmodel.toFormattedDate
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = hiltViewModel(),
    walkViewModel: WalkViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LocationAndActivityPermissions(onAllGranted = {})

    val isWalking by walkViewModel.isWalking.collectAsState()
    val routePoints by mapViewModel.routePoints.collectAsState()
    val currentLocation by mapViewModel.currentLocation.collectAsState()
    val natureSpots by mapViewModel.natureSpots.collectAsState()

    LaunchedEffect(isWalking) {
        if (isWalking) {
            mapViewModel.resetRoute()
            mapViewModel.startTracking()   // matches clean MapViewModel
        } else {
            mapViewModel.stopTracking()
        }
    }

    val defaultPosition = GeoPoint(65.0121, 25.4651)

    Column(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f)) {

            Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osmdroid", 0)
            )
            Configuration.getInstance().userAgentValue = context.packageName

            val mapViewState = remember { MapView(context) }
            var selectedSpot by remember { mutableStateOf<NatureSpot?>(null) }
            var hasCentered by remember { mutableStateOf(false) }

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

                    // Route polyline
                    if (routePoints.size >= 2) {
                        val polyline = Polyline().apply {
                            setPoints(routePoints)
                            outlinePaint.color = 0xFF2E7D32.toInt()
                            outlinePaint.strokeWidth = 8f
                        }
                        mapView.overlays.add(polyline)
                    }

                    // NatureSpot markers
                    natureSpots.forEach { spot ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(spot.latitude, spot.longitude)
                            title = spot.plantLabel ?: spot.name
                            snippet = spot.timestamp.toFormattedDate()

                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            val colorHex = getCategoryColorHex(spot.plantLabel ?: "unknown")
                            val tintedIcon = getTintedDefaultMarker(context, colorHex)

                            val safeIcon = tintedIcon
                                ?: mapView.context.getDrawable(
                                    org.osmdroid.library.R.drawable.marker_default
                                )!!

                            val zoom = mapView.zoomLevelDouble.toFloat()
                            val scale = (zoom / 5f).coerceIn(1.5f, 6f)
                            icon = scaleDrawable(safeIcon, scale)

                            setOnMarkerClickListener { _, _ ->
                                selectedSpot = spot
                                true
                            }
                        }

                        mapView.overlays.add(marker)
                    }

                    val loc = currentLocation
                    if (!hasCentered && loc != null) {
                        val startPoint = GeoPoint(loc.latitude, loc.longitude)
                        mapView.controller.setCenter(startPoint)
                        hasCentered = true
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
