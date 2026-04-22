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
    lat: Double? = null,
    lng: Double? = null,
    mapViewModel: MapViewModel = hiltViewModel(),
    walkViewModel: WalkViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
)
 {
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

    // OSMDroid config
    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", 0)
        )
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose {}
    }

    // ⭐ Delay MapView creation by one frame (fixes SIGKILL)
    var ready by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { ready = true }

    Column(Modifier.fillMaxSize()) {

        Box(Modifier.weight(1f)) {

            var selectedSpot by remember { mutableStateOf<NatureSpot?>(null) }
            var followUser by remember { mutableStateOf(true) }

            // Polyline remembered
            val polyline = remember {
                Polyline().apply {
                    outlinePaint.color = 0xFF2E7D32.toInt()
                    outlinePaint.strokeWidth = 8f
                    outlinePaint.isAntiAlias = true
                }
            }

            if (ready) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(15.0)
                            controller.setCenter(defaultPosition)

                            overlays.add(polyline)

                            setOnTouchListener { _, _ ->
                                followUser = false
                                false
                            }
                        }
                    },
                    update = { map ->

                        // Update polyline
                        polyline.setPoints(routePoints)

                        // Rebuild markers
                        map.overlays.removeAll { it is Marker }
                        natureSpots.forEach { spot ->
                            val marker = Marker(map).apply {
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
                            map.overlays.add(marker)
                        }

                        // Follow user
                        currentLocation?.let { loc ->
                            if (followUser) {
                                map.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
                            }
                        }

                        // ⭐ Jump to spot if opened from Timeline
                        if (lat != null && lng != null) {
                            followUser = false
                            map.controller.setZoom(17.0)
                            map.controller.animateTo(GeoPoint(lat, lng))
                        }


                        map.invalidate()
                    }
                )
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
