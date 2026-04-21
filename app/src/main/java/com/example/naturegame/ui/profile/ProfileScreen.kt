package com.example.naturegame.ui.profile

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.naturegame.viewmodel.ProfileViewModel
import com.example.naturegame.viewmodel.WalkViewModel

@Composable
fun ProfileScreen() {

    val viewModel: ProfileViewModel = hiltViewModel()
    val walkViewModel: WalkViewModel = hiltViewModel()

// Profile data
    val name by viewModel.profileName.collectAsState()
    val pictureUri by viewModel.profilePictureUri.collectAsState()
    val useMiles by viewModel.useMiles.collectAsState()

// ⭐ Lifetime totals (Activity section)
    val totalSteps by viewModel.totalSteps.collectAsState()
    val totalDistanceMeters by viewModel.totalDistance.collectAsState()
    val findings by viewModel.findingsCount.collectAsState()

    val steps = totalSteps

// ⭐ Current session (Badges)
    val currentSession by walkViewModel.currentSession.collectAsState()
    val sessionSteps = currentSession?.stepCount ?: 0
    val sessionDistance = currentSession?.distanceMeters ?: 0f

// ⭐ Badges use session values
    val badges = listOf(
        ProfileViewModel.Badge("walker", "Walker", sessionSteps >= 1000),
        ProfileViewModel.Badge("explorer", "Explorer", findings >= 5),
        ProfileViewModel.Badge("tracker", "Tracker", sessionDistance >= 1000f)
    )

// ⭐ Activity distance formatting (lifetime totals)
    val distanceKm = totalDistanceMeters / 1000f
    val displayDistance =
        if (useMiles) String.format("%.2f mi", distanceKm * 0.621371)
        else String.format("%.2f km", distanceKm)

    // Dialog state
    var showNameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(name) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {}
            viewModel.updatePicture(it.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))   // ⭐ New background
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🌿 Gradient behind profile picture
        Box(
            modifier = Modifier
                .size(170.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            MaterialTheme.colorScheme.background
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(140.dp)
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
                            .padding(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        // 🌿 Activity header
        SectionHeader(icon = Icons.Default.Insights, title = "Your Activity")

        Spacer(modifier = Modifier.height(16.dp))

        SectionBox {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatWithIcon(Icons.Default.DirectionsWalk, steps.toString(), "Steps")
                StatWithIcon(Icons.Default.Place, findings.toString(), "Discoveries")
                StatWithIcon(Icons.Default.Map, displayDistance, "Distance")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(32.dp))

        // 🌿 Badges header
        SectionHeader(icon = Icons.Default.EmojiEvents, title = "Badges")

        Spacer(modifier = Modifier.height(16.dp))

        SectionBox {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                badges.forEach { badge ->
                    BadgeItem(
                        label = badge.label,
                        icon = when (badge.id) {
                            "walker" -> Icons.Default.DirectionsWalk
                            "explorer" -> Icons.Default.Place
                            "tracker" -> Icons.Default.Map
                            else -> Icons.Default.Star
                        },
                        unlocked = badge.unlocked
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(32.dp))

        // 🌿 Settings header
        SectionHeader(icon = Icons.Default.Settings, title = "Settings")

        Spacer(modifier = Modifier.height(16.dp))

        SettingsRow("Change units") { viewModel.toggleUnits() }
        SettingsRow("Reset progress") { showResetDialog = true }
        SettingsRow("About") { showAboutDialog = true }
    }

    // -----------------------------
    // Dialogs
    // -----------------------------

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

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all progress?") },
            text = { Text("This will clear steps, distance, discoveries, and badges.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAll()
                    showResetDialog = false
                }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About NatureGame") },
            text = {
                Text("Version 1.0\nCreated by Yvonne\nA nature exploration and activity tracker.")
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
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
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 16.dp)
    ) {
        content()
    }
}

@Composable
fun StatWithIcon(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun BadgeItem(label: String, icon: ImageVector, unlocked: Boolean) {
    val tint = if (unlocked) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = tint)
        Text(label, style = MaterialTheme.typography.bodySmall, color = tint)
    }
}

@Composable
fun SettingsRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
