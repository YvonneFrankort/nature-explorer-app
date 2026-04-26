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
import com.example.naturegame.ui.timeline.TimelineScreen
import androidx.compose.runtime.collectAsState
import com.example.naturegame.viewmodel.MapViewModel
import androidx.compose.runtime.getValue
import com.example.naturegame.ui.login.LoginScreen
import com.example.naturegame.ui.login.RegisterScreen
import com.example.naturegame.data.remote.firebase.AuthManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun NatureGameNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val authManager = remember { AuthManager() }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {

        composable("login") {

            // Auto‑redirect if already logged in
            LaunchedEffect(Unit) {
                if (authManager.isSignedIn) {
                    navController.navigate("map") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("map") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                authManager = authManager
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("map") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("map") {
            MapScreen(
                navController = navController,
                authManager = authManager
            )
        }

        composable("map/{lat}/{lng}") { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull()

            MapScreen(
                navController = navController,
                authManager = authManager,
                lat = lat,
                lng = lng
            )
        }

        composable("camera") {
            val cameraViewModel: CameraViewModel = hiltViewModel()
            CameraScreen(
                navController = navController,
                authManager = authManager,
                cameraViewModel = cameraViewModel
            )
        }

        composable("discover") {
            DiscoverScreen(
                navController = navController,
                authManager = authManager
            )
        }

        composable("stats") {
            val statsViewModel: StatsViewModel = hiltViewModel()
            StatsScreen(
                navController = navController,
                authManager = authManager,
                statsViewModel = statsViewModel
            )
        }

        composable("profile") {
            ProfileScreen(
                navController = navController,
                authManager = authManager
            )
        }

        composable("timeline") {
            val statsViewModel: StatsViewModel = hiltViewModel()
            val mapViewModel: MapViewModel = hiltViewModel()

            val walks by statsViewModel.sessions.collectAsState()
            val spots by mapViewModel.natureSpots.collectAsState()

            TimelineScreen(
                navController = navController,
                authManager = authManager,
                walks = walks,
                spots = spots,
                onDiscoveryClick = { spot ->
                    navController.navigate("discovery/${spot.id}")
                },
                onMapClick = { spot ->
                    navController.navigate("map/${spot.latitude}/${spot.longitude}") {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo("map")
                    }
                }
            )
        }
    }
}
