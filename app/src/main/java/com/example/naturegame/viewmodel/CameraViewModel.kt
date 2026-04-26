package com.example.naturegame.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.data.repository.NatureSpotRepository
import com.example.naturegame.location.LocationManager
import com.example.naturegame.ml.ClassificationResult
import com.example.naturegame.ml.PlantClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import java.util.UUID

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application,
    private val repository: NatureSpotRepository,
    val locationManager: LocationManager
) : AndroidViewModel(application) {

    private val classifier = PlantClassifier()

    private val _capturedImagePath = MutableStateFlow<String?>(null)
    val capturedImagePath: StateFlow<String?> = _capturedImagePath.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _classificationResult = MutableStateFlow<ClassificationResult?>(null)
    val classificationResult: StateFlow<ClassificationResult?> = _classificationResult.asStateFlow()

    var currentNote = MutableStateFlow("")

    fun takePhotoAndClassify(context: Context, imageCapture: ImageCapture) {
        _isLoading.value = true

        viewModelScope.launch {
            val imagePath = takePhotoSuspend(context, imageCapture)
            if (imagePath == null) {
                _isLoading.value = false
                return@launch
            }

            _capturedImagePath.value = imagePath

            try {
                val uri = Uri.fromFile(File(imagePath))
                val result = classifier.classify(uri, context)
                _classificationResult.value = result
            } catch (e: Exception) {
                _classificationResult.value = ClassificationResult.Error(e.message ?: "Unknown error")
            }

            _isLoading.value = false
        }
    }

    private suspend fun takePhotoSuspend(
        context: Context,
        imageCapture: ImageCapture
    ): String? = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputDir = File(context.filesDir, "nature_photos").also { it.mkdirs() }
        val outputFile = File(outputDir, "IMG_${timestamp}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    continuation.resume(outputFile.absolutePath)
                }

                override fun onError(exception: ImageCaptureException) {
                    continuation.resume(null)
                }
            }
        )
    }

    fun clearCapturedImage() {
        _capturedImagePath.value = null
        _classificationResult.value = null
        currentNote.value = ""
    }

    fun saveCurrentSpot(context: Context) {
        val imagePath = _capturedImagePath.value ?: return

        viewModelScope.launch {
            _isLoading.value = true

            val result = _classificationResult.value

            val loc = locationManager.currentLocation.value
            val lat = loc?.latitude ?: 0.0
            val lon = loc?.longitude ?: 0.0

            val baseSpot = NatureSpot(
                id = UUID.randomUUID().toString(),
                name = when (result) {
                    is ClassificationResult.Success -> result.category
                    else -> "Nature spot"
                },
                latitude = lat,
                longitude = lon,

                imageLocalPath = imagePath,
                imageFirebaseUrl = null,

                plantLabel = (result as? ClassificationResult.Success)?.category,
                confidence = (result as? ClassificationResult.Success)?.confidence,

                note = currentNote.value,

                userId = repository.getCurrentUserId(),
                timestamp = System.currentTimeMillis(),

                synced = false
            )



            val firebaseUrl = try {
                repository.uploadImageToFirebase(imagePath, baseSpot.id)
            } catch (e: Exception) {
                null
            }

            val finalSpot = baseSpot.copy(imageFirebaseUrl = firebaseUrl)

            repository.insertSpot(finalSpot)

            android.widget.Toast.makeText(context, "Spot saved", android.widget.Toast.LENGTH_SHORT).show()

            clearCapturedImage()
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        classifier.close()
    }
}
