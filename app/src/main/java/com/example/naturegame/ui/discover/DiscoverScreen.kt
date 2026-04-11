package com.example.naturegame.ui.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Nature
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.util.toFormattedDate
import java.io.File

@Composable
fun DiscoverScreen(viewModel: DiscoverViewModel = viewModel()) {
    val spots by viewModel.allSpots.collectAsState()

    if (spots.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Outlined.Nature,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text("No discoveries yet", modifier = Modifier.padding(8.dp))
                Text(
                    "Take a photo of plants with the camera!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "${spots.size} discoveries",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(
                items = spots,
                key = { it.id }
            ) { spot ->
                NatureSpotCard(spot = spot, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NatureSpotCard(spot: NatureSpot, viewModel: DiscoverViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            val localFile = spot.imageLocalPath?.let { File(it) }
            val imageModel =
                if (localFile != null && localFile.exists()) localFile
                else spot.imageFirebaseUrl

            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = spot.plantLabel,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Nature, contentDescription = null)
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = spot.plantLabel ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    if (spot.synced) {
                        Icon(
                            Icons.Filled.Cloud,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            Icons.Filled.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                    }

                    IconButton(onClick = { viewModel.deleteSpot(spot) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }

                spot.confidence?.let { conf ->
                    Text(
                        text = "${"%.0f".format(conf * 100)}% confidence",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (conf > 0.8f) Color(0xFF2E7D32) else Color.Gray
                    )
                }

                Text(
                    text = spot.timestamp.toFormattedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (!spot.note.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = spot.note!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
