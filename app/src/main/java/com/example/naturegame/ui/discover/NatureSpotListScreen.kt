package com.example.naturegame.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.naturegame.data.local.entity.NatureSpot
import java.io.File

@Composable
fun NatureSpotListScreen(spots: List<NatureSpot>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(spots) { spot ->
            NatureSpotItem(spot)
        }
    }
}

@Composable
fun NatureSpotItem(spot: NatureSpot) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val context = LocalContext.current

        // Decide what to load: local file first, then Firebase URL
        val data = spot.imageLocalPath?.let { File(it) } ?: spot.imageFirebaseUrl

        val request = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build()

        AsyncImage(
            model = request,
            contentDescription = spot.plantLabel ?: "Nature photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = spot.plantLabel ?: "Unknown plant",
            style = MaterialTheme.typography.titleMedium
        )
        spot.note?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        spot.latitude?.let { lat ->
            spot.longitude?.let { lon ->
                Text(
                    text = "Location: $lat, $lon",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
