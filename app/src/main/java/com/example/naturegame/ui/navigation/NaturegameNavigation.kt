package com.example.naturegame.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.naturegame.location.LocationManager

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
        composable(Screen.Map.route) {
            MapScreen()
        }

        composable(Screen.Camera.route) {
            val cameraViewModel: CameraViewModel = viewModel()

            val context = LocalContext.current
            val locationManager = remember { LocationManager(context) }

            // Start GPS tracking when entering the screen
            LaunchedEffect(Unit) {
                locationManager.startTracking()
            }

            CameraScreen(
                cameraViewModel = cameraViewModel,
                locationManager = locationManager
            )
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
    }
}
