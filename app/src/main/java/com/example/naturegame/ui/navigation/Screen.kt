package com.example.naturegame.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Map : Screen("map", "Map", Icons.Filled.Map)

    object Camera : Screen("camera", "Camera", Icons.Filled.CameraAlt)

    object Discover : Screen("discover", "Explore", Icons.Filled.Explore)

    object Stats : Screen("stats", "Stats", Icons.Filled.BarChart)

    object Profile : Screen("profile", "Profile", Icons.Filled.Person)

    object Timeline : Screen("timeline", "History", Icons.Filled.History)

    companion object {
        val bottomNavScreens = listOf(Map, Camera, Discover, Stats, Timeline, Profile)
    }
}