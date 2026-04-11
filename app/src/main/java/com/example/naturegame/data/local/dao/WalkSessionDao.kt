package com.example.naturegame.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.naturegame.data.local.entity.WalkSession
import kotlinx.coroutines.flow.Flow
import androidx.room.Update

@Dao
interface WalkSessionDao {

    @Insert
    suspend fun insert(session: WalkSession)

    @Update
    suspend fun update(session: WalkSession)

    @Query("SELECT * FROM walk_sessions WHERE isActive = 1 LIMIT 1")
    fun getActiveSession(): Flow<WalkSession?>

    @Query("UPDATE walk_sessions SET isActive = 0, endTime = :endTime WHERE id = :id")
    suspend fun endSession(id: String, endTime: Long)

    @Query("SELECT * FROM walk_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WalkSession>>
}
