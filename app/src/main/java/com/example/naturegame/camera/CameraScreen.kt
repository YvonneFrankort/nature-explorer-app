package com.example.naturegame.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.naturegame.location.LocationManager
import com.example.naturegame.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    locationManager: LocationManager
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Listen to GPS updates and forward them to the ViewModel
    val location by locationManager.currentLocation.collectAsState()

    LaunchedEffect(location) {
        location?.let {
            cameraViewModel.updateLocation(it.latitude, it.longitude)
        }
    }


    val viewModel = cameraViewModel

    val capturedImagePath by viewModel.capturedImagePath.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // CameraX components
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Permission handling
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // If permission missing → show UI
    if (!hasPermission) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text("Camera permission required")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant permission")
                }
            }
        }
        return
    }

    // Bind camera AFTER previewView exists
    LaunchedEffect(previewView, hasPermission) {
        if (!hasPermission) return@LaunchedEffect

        val cameraProvider = ProcessCameraProvider.getInstance(context).get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (_: Exception) {}
    }

    // UI
    Box(Modifier.fillMaxSize()) {

        if (capturedImagePath == null) {

            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                Modifier.fillMaxSize().padding(32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                FloatingActionButton(
                    onClick = { viewModel.takePhotoAndClassify(context, imageCapture) }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(Icons.Default.Camera, contentDescription = null)
                    }
                }
            }

        } else {

            val note by viewModel.currentNote.collectAsState()
            val classificationResult by viewModel.classificationResult.collectAsState()

            // Only CapturedImageView now — classification is inside it
            CapturedImageView(
                imagePath = capturedImagePath!!,
                note = note,
                classificationResult = classificationResult,
                onNoteChange = { viewModel.currentNote.value = it },
                onCancelNote = { viewModel.currentNote.value = "" },
                onRetake = { viewModel.clearCapturedImage() },
                onSave = { viewModel.saveCurrentSpot(context) }
            )
        }
    }
}
