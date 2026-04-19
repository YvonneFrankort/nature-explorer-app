package com.example.naturegame.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturegame.viewmodel.ProfileViewModel
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen() {

    val viewModel: ProfileViewModel = hiltViewModel()

    // Collect state
    val name by viewModel.profileName.collectAsState()
    val pictureUri by viewModel.profilePictureUri.collectAsState()
    val steps by viewModel.totalSteps.collectAsState()
    val findings by viewModel.findingsCount.collectAsState()

    // Dialog state
    var showNameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }

    // distance
    val distanceMeters by viewModel.totalDistance.collectAsState()
    val distanceKm = distanceMeters / 1000f

    // Image picker
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // ⭐ FIX: Persist permission so the URI survives app restarts
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some providers don't support persistable permissions — safe to ignore
            }

            viewModel.updatePicture(it.toString())
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Profile picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") }
        ) {
            if (pictureUri.isNotEmpty()) {
                AsyncImage(
                    model = pictureUri,
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                newName = name
                showNameDialog = true
            }
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit name",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats row (NO calories anymore)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(steps.toString(), "Steps")
            StatItem(findings.toString(), "Discoveries")
            StatItem(String.format("%.2f km", distanceKm), "Distance")
        }
    }

    // Name dialog
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Edit name") },
            text = {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateName(newName)
                    showNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
