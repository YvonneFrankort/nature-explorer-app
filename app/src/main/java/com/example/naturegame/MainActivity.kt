package com.example.naturegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.naturegame.ui.navigation.NatureGameBottomBar
import com.example.naturegame.ui.navigation.NatureGameNavigation
import com.example.naturegame.ui.theme.NatureGameTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import androidx.compose.runtime.CompositionLocalProvider
import com.example.naturegame.ui.utils.LocalActivity

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        Log.d("FIREBASE_TEST", "Auth instance: ${FirebaseAuth.getInstance()}")

        installSplashScreen()

        setContent {
            CompositionLocalProvider(
                LocalActivity provides this
            ) {
                NatureGameTheme {
                    NatureGameApp()
                }
            }
        }
    }
}

@Composable
fun NatureGameApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NatureGameBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NatureGameNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}