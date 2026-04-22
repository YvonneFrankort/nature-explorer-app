package com.example.naturegame.ui.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.viewmodel.toFormattedDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import com.example.naturegame.utils.getCategoryColor

@Composable
fun DiscoveryTimelineCard(
    spot: NatureSpot,
    onClick: () -> Unit,
    onMapClick: (() -> Unit)? = null
) {
    val imageSource = spot.imageLocalPath ?: spot.imageFirebaseUrl
    val timeFormatted = spot.timestamp.toFormattedDate()

    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() }
    ) {
        // Vertical timeline stripe
        Box(
            Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(Color(0xFF90CAF9))
        )

        Spacer(Modifier.width(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(Modifier.padding(16.dp)) {

                // ⭐ Category dot moved left of the image
                CategoryDot(color = getCategoryColor(spot.plantLabel ?: ""))
                Spacer(Modifier.width(12.dp))

                Image(
                    painter = rememberAsyncImagePainter(imageSource),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        text = spot.plantLabel ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        timeFormatted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                onMapClick?.let {
                    IconButton(onClick = it) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Open on map"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryDot(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(RoundedCornerShape(50))
            .background(color)
    )
}
