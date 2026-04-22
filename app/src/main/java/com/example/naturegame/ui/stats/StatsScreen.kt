package com.example.naturegame.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.naturegame.viewmodel.StatsViewModel
import com.example.naturegame.viewmodel.toFormattedDate
import com.example.naturegame.data.local.entity.WalkSession
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel
) {
    val sessions by statsViewModel.sessions.collectAsStateWithLifecycle()
    val totalSteps by statsViewModel.totalSteps.collectAsStateWithLifecycle()
    val totalDistance by statsViewModel.totalDistance.collectAsStateWithLifecycle()
    val totalWalks by statsViewModel.totalWalks.collectAsStateWithLifecycle()

    // ⭐ Longest walk
    val longestWalk = sessions.maxByOrNull { it.distanceMeters }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
    ) {

        Text(
            text = "Your Walk Statistics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        SummaryCard("Total Steps: ", totalSteps.toString(), Icons.Default.DirectionsWalk)
        SummaryCard("Total Distance: ", formatDistance(totalDistance), Icons.Default.Map)
        SummaryCard("Total Walks: ", totalWalks.toString(), Icons.Default.Timer)

        if (longestWalk != null) {
            HighlightBox("Longest walk: ${formatDistance(longestWalk.distanceMeters)}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Walk History",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionBox {
            LazyColumn {
                items(sessions) { session ->
                    WalkSessionItem(session)
                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun WalkSessionItem(session: WalkSession) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Walk on ${session.startTime.toFormattedDate()}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("Steps: ${session.stepCount}")
            Text("Distance: ${formatDistance(session.distanceMeters)}")
            Text("Duration: ${formatDuration(session.startTime, session.endTime ?: session.startTime)}")
        }
    }
}

@Composable
fun HighlightBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SectionBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 12.dp)
    ) {
        content()
    }
}

fun formatDistance(meters: Float): String {
    return if (meters < 1000f) {
        "${meters.toInt()} m"
    } else {
        "%.1f km".format(meters / 1000f)
    }
}

fun formatDuration(startTime: Long, endTime: Long = System.currentTimeMillis()): String {
    val seconds = (endTime - startTime) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> "${hours}h ${minutes % 60}min"
        minutes > 0 -> "${minutes}min ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
