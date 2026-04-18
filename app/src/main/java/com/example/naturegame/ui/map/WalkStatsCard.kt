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

@Composable
fun WalkStatsCard(
    viewModel: WalkViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {

    val session by viewModel.currentSession.collectAsState()
    val isWalking by viewModel.isWalking.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isWalking) "Walk in progress" else "Walk stopped",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            session?.let { s ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${s.stepCount}", style = MaterialTheme.typography.headlineMedium)
                        Text("steps")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            formatDistance(s.distanceMeters),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("distance")
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            formatDuration(s.startTime, s.endTime ?: System.currentTimeMillis()),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text("time")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                if (!isWalking) {
                    Button(
                        onClick = { viewModel.startWalk() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Start walk") }
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.stopWalk { session ->
                                // callback if needed
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Stop") }
                }
            }
        }
    }
}
