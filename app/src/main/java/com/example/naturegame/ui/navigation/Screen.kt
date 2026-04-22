package com.example.naturegame.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Sealed class kuvaa kaikki sovelluksen reitit
// Sealed class = rajoitettu periytyminen, kaikki aliluokat tunnetaan käännösaikana
sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    // Karttanäkymä: GPS-reitti ja havaintojen sijainnit
    object Map : Screen("map", "Map", Icons.Filled.Map)
    // Kameranäkymä: CameraX-esikatselu + kuvaaminen
    object Camera : Screen("camera", "Camera", Icons.Filled.CameraAlt)
    // Löydöt: muiden käyttäjien havainnot Firebasesta
    object Discover : Screen("discover", "Explore", Icons.Filled.Explore)
    // Tilastot: askeleet, matka, omat havainnot
    object Stats : Screen("stats", "Stats", Icons.Filled.BarChart)

    object Profile : Screen("profile", "Profile", Icons.Filled.Person)

    object Timeline : Screen("timeline", "History", Icons.Filled.History)

    companion object {
        // Lista kaikista bottom nav -kohteista
        val bottomNavScreens = listOf(Map, Camera, Discover, Stats, Timeline, Profile)
    }
}