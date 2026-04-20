package com.example.naturegame.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.naturegame.viewmodel.WalkViewModel
import com.example.naturegame.viewmodel.ProfileViewModel
import com.example.naturegame.ui.stats.formatDuration
import com.example.naturegame.ui.stats.formatDistance
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween

@Composable
fun WalkStatsCard(
    viewModel: WalkViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val session by viewModel.currentSession.collectAsState()
    val isWalking by viewModel.isWalking.collectAsState()

    val enterAnim = remember {
        androidx.compose.animation.core.tween<Float>(
            durationMillis = 400
        )
    }

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(initialOffsetY = { it / 4 })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header
                Text(
                    text = if (isWalking) "Walk in progress" else "Walk stopped",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Stats row
                session?.let { s ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        StatItem(
                            label = "Steps",
                            value = s.stepCount.toString()
                        )

                        StatItem(
                            label = "Distance",
                            value = formatDistance(s.distanceMeters)
                        )

                        StatItem(
                            label = "Time",
                            value = formatDuration(
                                s.startTime,
                                s.endTime ?: System.currentTimeMillis()
                            )
                        )
                    }
                }

                // Start/Stop button
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (!isWalking) {
                        Button(
                            onClick = { viewModel.startWalk() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Start walk")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.stopWalk { } },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Stop")
                        }
                    }
                }
            }
        }
    }
}


    @Composable
    private fun StatItem(label: String, value: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }



