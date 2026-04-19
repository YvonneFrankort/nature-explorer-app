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
            mapViewModel.startTracking()
        } else {
            mapViewModel.stopTracking()
        }
    }

    val defaultPosition = GeoPoint(65.0121, 25.4651)

    Column(Modifier.fillMaxSize()) {

        Box(Modifier.weight(1f)) {

            // OSMDroid config
            DisposableEffect(Unit) {
                Configuration.getInstance().load(
                    context,
                    context.getSharedPreferences("osmdroid", 0)
                )
                Configuration.getInstance().userAgentValue = context.packageName
                onDispose {}
            }

            // MapView created once
            val mapView = remember { MapView(context) }

            var selectedSpot by remember { mutableStateOf<NatureSpot?>(null) }
            var followUser by remember { mutableStateOf(true) }

            // Polyline created once
            val polyline = remember {
                Polyline().apply {
                    outlinePaint.color = 0xFF2E7D32.toInt()
                    outlinePaint.strokeWidth = 8f
                    outlinePaint.isAntiAlias = true
                }
            }

            // Marker list remembered
            val markers = remember { mutableStateListOf<Marker>() }

            // Setup map once
            DisposableEffect(Unit) {
                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.setMultiTouchControls(true)
                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(defaultPosition)

                // Add polyline once
                if (!mapView.overlays.contains(polyline)) {
                    mapView.overlays.add(polyline)
                }

                onDispose { mapView.onDetach() }
            }

            // Update markers when natureSpots changes
            LaunchedEffect(natureSpots) {
                markers.clear()
                natureSpots.forEach { spot ->
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(spot.latitude, spot.longitude)
                        title = spot.plantLabel ?: spot.name
                        snippet = spot.timestamp.toFormattedDate()

                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        val colorHex = getCategoryColorHex(spot.plantLabel ?: "unknown")
                        val tintedIcon = getTintedDefaultMarker(context, colorHex)
                        val safeIcon = tintedIcon
                            ?: context.getDrawable(org.osmdroid.library.R.drawable.marker_default)!!

                        icon = scaleDrawable(safeIcon, 2f)

                        setOnMarkerClickListener { _, _ ->
                            selectedSpot = spot
                            true
                        }
                    }
                    markers.add(marker)
                }
            }

            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize(),
                update = { map ->

                    // Update polyline points
                    polyline.setPoints(routePoints)

                    // Add markers if missing
                    markers.forEach { marker ->
                        if (!map.overlays.contains(marker)) {
                            map.overlays.add(marker)
                        }
                    }

                    // Follow user
                    currentLocation?.let { loc ->
                        if (followUser) {
                            map.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
                        }
                    }

                    map.invalidate()
                }
            )

            // Stop following when user touches map
            DisposableEffect(Unit) {
                mapView.setOnTouchListener { _, _ ->
                    followUser = false
                    false
                }
                onDispose {}
            }

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