package com.example.naturegame.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.naturegame.data.local.entity.NatureSpot


class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val spotsCollection = db.collection("nature_spots")

    // Save full NatureSpot to Firestore
    suspend fun saveSpot(spot: NatureSpot): Result<Unit> {
        return try {
            val data = mapOf(
                "id" to spot.id,
                "name" to spot.name,
                "latitude" to spot.latitude,
                "longitude" to spot.longitude,
                "plantLabel" to spot.plantLabel,
                "confidence" to spot.confidence,
                "imageLocalPath" to spot.imageLocalPath,
                "imageFirebaseUrl" to spot.imageFirebaseUrl,
                "userId" to spot.userId,
                "timestamp" to spot.timestamp,
                "synced" to spot.synced,
                "note" to spot.note
            )

            spotsCollection.document(spot.id).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Load full NatureSpot from Firestore
    fun getUserSpots(userId: String): Flow<List<NatureSpot>> = callbackFlow {
        val listener = spotsCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val spots = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        NatureSpot(
                            id = doc.getString("id") ?: return@mapNotNull null,
                            name = doc.getString("name") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0,
                            plantLabel = doc.getString("plantLabel"),
                            confidence = doc.getDouble("confidence")?.toFloat(),
                            imageLocalPath = doc.getString("imageLocalPath"),
                            imageFirebaseUrl = doc.getString("imageFirebaseUrl"),
                            userId = doc.getString("userId"),
                            timestamp = doc.getLong("timestamp") ?: 0L,
                            synced = doc.getBoolean("synced") ?: true
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(spots)
            }

        awaitClose { listener.remove() }
    }
}
