package com.example.naturegame.ml

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PlantClassifier {

    // Lower threshold → ML Kit gives more usable labels
    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.3f)
            .build()
    )

    // Expanded nature keywords
    private val natureKeywords = mapOf(
        "Flower" to listOf("flower", "blossom", "bloom"),
        "Plant" to listOf("plant", "flora", "vegetation", "leaf", "herb", "botanical"),
        "Tree" to listOf("tree", "branch", "bark", "forest", "woodland"),
        "Mushroom" to listOf("mushroom", "fungus", "toadstool"),
        "Animal" to listOf("animal", "mammal", "wildlife"),
        "Bird" to listOf("bird", "nest", "avian"),
        "Insect" to listOf("insect", "bug", "arthropod", "invertebrate", "butterfly", "bee"),
        "Landscape" to listOf("landscape", "mountain", "river", "lake", "waterfall", "sky", "coast", "outdoor"),
        "Rock" to listOf("rock", "stone", "boulder", "natural object")
    )

    suspend fun classify(imageUri: Uri, context: Context): ClassificationResult {
        return suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromFilePath(context, imageUri)

                labeler.process(inputImage)
                    .addOnSuccessListener { labels ->

                        // Find best matching nature category
                        val bestMatch = findBestNatureCategory(labels)

                        val result = if (bestMatch != null) {
                            ClassificationResult.Success(
                                category = bestMatch.first,
                                confidence = bestMatch.second.confidence,
                                allLabels = labels.take(5)
                            )
                        } else {
                            ClassificationResult.NotNature(
                                allLabels = labels.take(3)
                            )
                        }

                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun findBestNatureCategory(labels: List<ImageLabel>): Pair<String, ImageLabel>? {
        var bestCategory: String? = null
        var bestLabel: ImageLabel? = null

        for (label in labels) {
            for ((category, keywords) in natureKeywords) {
                if (keywords.any { keyword -> label.text.contains(keyword, ignoreCase = true) }) {
                    if (bestLabel == null || label.confidence > bestLabel!!.confidence) {
                        bestCategory = category
                        bestLabel = label
                    }
                }
            }
        }

        return if (bestCategory != null && bestLabel != null) {
            bestCategory!! to bestLabel!!
        } else null
    }

    fun close() {
        labeler.close()
    }
}

sealed class ClassificationResult {
    data class Success(
        val category: String,
        val confidence: Float,
        val allLabels: List<ImageLabel>
    ) : ClassificationResult()

    data class NotNature(
        val allLabels: List<ImageLabel>
    ) : ClassificationResult()

    data class Error(val message: String) : ClassificationResult()
}
