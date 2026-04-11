package com.example.naturegame.data.repository

import com.example.naturegame.data.local.dao.NatureSpotDao
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.data.remote.firebase.AuthManager
import com.example.naturegame.data.remote.firebase.FirestoreManager
import com.example.naturegame.data.remote.firebase.StorageManager
import kotlinx.coroutines.flow.Flow

class NatureSpotRepository(
    private val dao: NatureSpotDao,
    private val firestoreManager: FirestoreManager,
    private val storageManager: StorageManager,
    private val authManager: AuthManager
) {

    // Local Room data (offline-first)
    val allSpots: Flow<List<NatureSpot>> = dao.getAllSpots()

    // Insert a new spot: save locally first, then sync to Firebase
    suspend fun insertSpot(spot: NatureSpot) {
        val spotWithUser = spot.copy(userId = authManager.currentUserId)

        // 1. Save locally immediately (works offline)
        dao.insert(spotWithUser.copy(synced = false))

        // 2. Try to sync to Firebase (metadata + mark synced)
        syncSpotToFirebase(spotWithUser)
    }

    // Called from ViewModel: upload image and return download URL
    suspend fun uploadImageToFirebase(localPath: String, spotId: String): String? {
        return storageManager.uploadImage(localPath, spotId).getOrNull()
    }

    fun getCurrentUserId(): String? {
        return authManager.currentUserId
    }

    // Sync a single spot to Firebase (no more image upload here)
    private suspend fun syncSpotToFirebase(spot: NatureSpot) {
        try {
            // Firebase URL already set by ViewModel
            val firebaseImageUrl = spot.imageFirebaseUrl

            // Save metadata to Firestore
            firestoreManager.saveSpot(spot.copy(imageFirebaseUrl = firebaseImageUrl)).getOrThrow()

            // Mark as synced in Room
            dao.markSynced(spot.id, firebaseImageUrl ?: "")
        } catch (e: Exception) {
            // leave unsynced
        }
    }

    suspend fun syncPendingSpots() {
        val unsyncedSpots = dao.getUnsyncedSpots()
        unsyncedSpots.forEach { spot ->
            syncSpotToFirebase(spot)
        }
    }
    suspend fun deleteSpot(spot: NatureSpot) {
        dao.delete(spot)
    }
}
