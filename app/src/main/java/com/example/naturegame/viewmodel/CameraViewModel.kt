package com.example.naturegame.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.AppDatabase
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.data.repository.NatureSpotRepository
import com.example.naturegame.ml.ClassificationResult
import com.example.naturegame.ml.PlantClassifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.example.naturegame.data.remote.firebase.FirestoreManager
import com.example.naturegame.data.remote.firebase.StorageManager
import com.example.naturegame.data.remote.firebase.AuthManager

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NatureSpotRepository(
        dao = AppDatabase.getDatabase(application).natureSpotDao(),
        firestoreManager = FirestoreManager(),
        storageManager = StorageManager(),
        authManager = AuthManager()
    )



    private val classifier = PlantClassifier()

    private val _capturedImagePath = MutableStateFlow<String?>(null)
    val capturedImagePath: StateFlow<String?> = _capturedImagePath.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _classificationResult = MutableStateFlow<ClassificationResult?>(null)
    val classificationResult: StateFlow<ClassificationResult?> = _classificationResult.asStateFlow()

    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0

    var currentNote = MutableStateFlow("")


    // -----------------------------
    //  Take photo + classify
    // -----------------------------
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

    // -----------------------------
    //  Suspend photo capture
    // -----------------------------
    private suspend fun takePhotoSuspend(
        context: Context,
        imageCapture: ImageCapture
    ): String? = kotlinx.coroutines.suspendCancellableCoroutine<String?> { continuation ->

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

    // -----------------------------
    //  Save spot with ML result
    // -----------------------------
    fun saveCurrentSpot(context: Context) {
        val imagePath = _capturedImagePath.value ?: return

        viewModelScope.launch {
            _isLoading.value = true

            val result = _classificationResult.value

            // 1. Create the base spot so we have its ID
            val baseSpot = NatureSpot(
                name = when (result) {
                    is ClassificationResult.Success -> result.category
                    else -> "Nature spot"
                },
                latitude = currentLatitude,
                longitude = currentLongitude,
                imageLocalPath = imagePath,
                plantLabel = (result as? ClassificationResult.Success)?.category,
                confidence = (result as? ClassificationResult.Success)?.confidence,
                userId = repository.getCurrentUserId(),
                note = currentNote.value
            )

            // 2. Upload image using the spot ID
            val firebaseUrl = try {
                repository.uploadImageToFirebase(imagePath, baseSpot.id)
            } catch (e: Exception) {
                null
            }

            // 3. Create final spot with Firebase URL
            val finalSpot = baseSpot.copy(imageFirebaseUrl = firebaseUrl)

            // 4. Save to Room + Firestore
            repository.insertSpot(finalSpot)

            android.widget.Toast.makeText(context, "Spot saved", android.widget.Toast.LENGTH_SHORT).show()

            clearCapturedImage()
            _isLoading.value = false
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        currentLatitude = lat
        currentLongitude = lon
    }

    override fun onCleared() {
        super.onCleared()
        classifier.close()
    }
}
