package com.example.naturegame.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.naturegame.data.local.entity.NatureSpot

@Dao
interface NatureSpotDao {

    // Lisää uusi kohde (tai korvaa jos sama id)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spot: NatureSpot): Long

    // Kaikki kohteet uusimmasta vanhimpaan (Flow päivittää UI:n automaattisesti)
    @Query("SELECT * FROM nature_spots ORDER BY timestamp DESC")
    fun getAllSpots(): Flow<List<NatureSpot>>

    // Hae sijainnin mukaan kartalle
    @Query("SELECT * FROM nature_spots WHERE latitude != 0.0 AND longitude != 0.0")
    fun getSpotsWithLocation(): Flow<List<NatureSpot>>

    // Hae synkronoimattomat (Firebase-lähetystä varten)
    @Query("SELECT * FROM nature_spots WHERE synced = 0")
    suspend fun getUnsyncedSpots(): List<NatureSpot>

    // Merkitse synkronoiduksi
    @Query("UPDATE nature_spots SET synced = 1, imageFirebaseUrl = :url WHERE id = :id")
    suspend fun markSynced(id: String, url: String)

    @Query("UPDATE nature_spots SET plantLabel = :label, confidence = :confidence WHERE id = :id")
    suspend fun updatePlantLabel(id: String, label: String, confidence: Float)

    @Delete
    suspend fun delete(spot: NatureSpot)

    @Query("SELECT COUNT(*) FROM nature_spots")
    fun getNatureSpotCount(): Flow<Int>
}