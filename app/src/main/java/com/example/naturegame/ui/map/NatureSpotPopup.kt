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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Category color bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            Color(android.graphics.Color.parseColor(
                                getCategoryColorHex(spot.plantLabel ?: "unknown")
                            ))
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ⭐ Image thumbnail
                spot.imageLocalPath?.let { path ->
                    Image(
                        painter = rememberAsyncImagePainter(File(path)),
                        contentDescription = "Nature spot image",
                        modifier = Modifier
                            .size(180.dp)
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Category name
                Text(
                    text = spot.plantLabel ?: spot.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date + time
                Text(
                    text = spot.timestamp.toFormattedDate(),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // User note
                spot.note?.takeIf { it.isNotBlank() }?.let { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}
