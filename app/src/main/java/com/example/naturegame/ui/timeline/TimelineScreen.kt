package com.example.naturegame.ui.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.data.local.entity.WalkSession
import com.example.naturegame.utils.groupSpotsByDate
import kotlinx.coroutines.launch
import kotlinx.coroutines.android.awaitFrame
import androidx.navigation.NavController
import com.example.naturegame.data.remote.firebase.AuthManager
import androidx.compose.runtime.LaunchedEffect

@Composable
fun TimelineScreen(
    navController: NavController,
    authManager: AuthManager,
    walks: List<WalkSession>,
    spots: List<NatureSpot>,
    onDiscoveryClick: (NatureSpot) -> Unit = {},
    onWalkClick: (WalkSession) -> Unit = {},
    onMapClick: (NatureSpot) -> Unit = {}
)
{

    if (!authManager.isSignedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("timeline") { inclusive = true }
            }
        }
        return
    }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val grouped = remember(spots) {
        groupSpotsByDate(spots)
    }

    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            grouped.forEach { (header, itemsForDay) ->

                item {
                    Text(
                        text = header,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(itemsForDay) { spot ->
                    DiscoveryTimelineCard(
                        spot = spot,
                        onClick = { onDiscoveryClick(spot) },
                        onMapClick = {
                            scope.launch {
                                awaitFrame()
                                onMapClick(spot)
                            }
                        }
                    )
                }
            }
        }

        if (showScrollToTop) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}
