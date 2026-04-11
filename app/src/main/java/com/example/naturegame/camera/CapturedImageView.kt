package com.example.naturegame.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.naturegame.ml.ClassificationResult
import java.io.File

@Composable
fun CapturedImageView(
    imagePath: String,
    note: String,
    classificationResult: ClassificationResult?,
    onNoteChange: (String) -> Unit,
    onCancelNote: () -> Unit,
    onRetake: () -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        // --- IMAGE ---
        AsyncImage(
            model = File(imagePath),
            contentDescription = "Captured photo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        )

        // --- CLASSIFICATION CARD ---
        if (classificationResult != null) {
            ClassificationResultCard(result = classificationResult)
        }

        // --- NOTE FIELD ---
        TextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Note") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

// --- NOTE ACTIONS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(onClick = onCancelNote) {
                Text("Cancel Note")
            }
        }

        // --- RETAKE / SAVE SPOT ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onRetake) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Retake")
            }

            Button(onClick = onSave) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Save Spot")
            }
        }
    }
}

@Composable
fun ClassificationResultCard(result: ClassificationResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(12.dp)) {

            when (result) {

                is ClassificationResult.Success -> {
                    Text(result.category, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Confidence: ${(result.confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(6.dp))
                    Text("Top labels:", style = MaterialTheme.typography.bodySmall)

                    result.allLabels.take(3).forEach {
                        Text(
                            "- ${it.text} (${(it.confidence * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                is ClassificationResult.NotNature -> {
                    Text("Not a nature object")
                    Spacer(Modifier.height(6.dp))
                    result.allLabels.take(3).forEach {
                        Text(
                            "- ${it.text} (${(it.confidence * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                is ClassificationResult.Error -> {
                    Text("Error: ${result.message}")
                }
            }
        }
    }
}
