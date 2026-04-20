package com.example.naturegame.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.utils.getCategoryColorHex
import com.example.naturegame.viewmodel.toFormattedDate
import java.io.File
import androidx.compose.ui.draw.clip

@Composable
fun NatureSpotPopup(
    spot: NatureSpot,
    onDismiss: () -> Unit
) {
    // Dimmed background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Category color stripe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(
                            Color(
                                android.graphics.Color.parseColor(
                                    getCategoryColorHex(spot.plantLabel ?: "unknown")
                                )
                            )
                        )
                )

                // Image (local or remote)
                val localFile = spot.imageLocalPath?.let { File(it) }
                val imageModel =
                    if (localFile != null && localFile.exists()) localFile
                    else spot.imageFirebaseUrl

                if (imageModel != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageModel),
                        contentDescription = "Nature spot image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Title
                Text(
                    text = spot.plantLabel ?: spot.name,
                    style = MaterialTheme.typography.titleLarge
                )

                // Date
                Text(
                    text = spot.timestamp.toFormattedDate(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Note
                spot.note?.takeIf { it.isNotBlank() }?.let { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 4
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
