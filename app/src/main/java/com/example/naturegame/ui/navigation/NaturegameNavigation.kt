package com.example.naturegame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.naturegame.ui.map.MapScreen
import com.example.naturegame.camera.CameraScreen
import com.example.naturegame.ui.discover.DiscoverScreen
import com.example.naturegame.ui.profile.ProfileScreen
import com.example.naturegame.ui.stats.StatsScreen
import com.example.naturegame.viewmodel.CameraViewModel
import com.example.naturegame.viewmodel.StatsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naturegame.ui.timeline.TimelineScreen
import androidx.compose.runtime.collectAsState
import com.example.naturegame.viewmodel.MapViewModel
import com.example.naturegame.viewmodel.WalkViewModel
import androidx.compose.runtime.getValue

@Composable
fun NatureGameNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route,
        modifier = modifier
    ) {
        // Normal map (bottom nav)
        composable(Screen.Map.route) {
            MapScreen()
        }

// Map with coordinates (timeline jump)
        composable(
            route = "map/{lat}/{lng}"
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()
            MapScreen(lat = lat, lng = lng)
        }


        composable(Screen.Camera.route) {
            val cameraViewModel: CameraViewModel = hiltViewModel()
            CameraScreen(cameraViewModel)
        }

        composable(Screen.Discover.route) {
            DiscoverScreen()
        }

        composable(Screen.Stats.route) {
            val statsViewModel: StatsViewModel = viewModel()
            StatsScreen(statsViewModel)
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.Timeline.route) {
            val statsViewModel: StatsViewModel = hiltViewModel()
            val mapViewModel: MapViewModel = hiltViewModel()

            val walks by statsViewModel.sessions.collectAsState()
            val spots by mapViewModel.natureSpots.collectAsState()

            TimelineScreen(
                walks = walks,
                spots = spots,
                onDiscoveryClick = { spot ->
                    navController.navigate("discovery/${spot.id}")
                },
                onMapClick = { spot ->
                    navController.navigate("map/${spot.latitude}/${spot.longitude}") {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Screen.Map.route)
                    }
                }
            )
        }
    }
    }
