package com.example.naturegame.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class StorageManager {
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(localFilePath: String, spotId: String): Result<String> {
        return try {
            val file = Uri.fromFile(File(localFilePath))

            val storageRef = storage.reference
                .child("spots")
                .child(spotId)
                .child("image.jpg")

            storageRef.putFile(file).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteImage(spotId: String): Result<Unit> {
        return try {
            storage.reference.child("spots/${spotId}/image.jpg")
                .delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}