package com.example.naturegame.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.naturegame.viewmodel.StatsViewModel
import com.example.naturegame.viewmodel.toFormattedDate
import com.example.naturegame.viewmodel.formatDuration
import com.example.naturegame.data.local.entity.WalkSession

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel
) {
    val sessions by statsViewModel.sessions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Your Walk Statistics",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        SummaryCard(
            title = "Total Steps",
            value = statsViewModel.totalSteps().toString()
        )

        SummaryCard(
            title = "Total Distance",
            value = formatDistance(statsViewModel.totalDistance())
        )

        SummaryCard(
            title = "Total Walks",
            value = statsViewModel.totalWalks().toString()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Walk History",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(sessions) { session ->
                WalkSessionItem(session)
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
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

fun formatDistance(meters: Float): String {
    return if (meters < 1000f) {
        "${meters.toInt()} m"
    } else {
        "%.1f km".format(meters / 1000f)
    }
}
