package com.example.naturegame.data.repository

import com.example.naturegame.data.local.dao.WalkSessionDao
import com.example.naturegame.data.local.entity.WalkSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WalkRepository(
    private val walkSessionDao: WalkSessionDao
) {

    fun getAllSessions(): Flow<List<WalkSession>> =
        walkSessionDao.getAllSessions()

    suspend fun insertSession(session: WalkSession) {
        walkSessionDao.insert(session)
    }

    suspend fun getTotalDistance(): Float {
        val sessions = walkSessionDao.getAllSessionsOnce()
        return sessions.sumOf { it.distanceMeters.toDouble() }.toFloat()
    }
    fun getTotalDistanceFlow(): Flow<Float> =
        walkSessionDao.getAllSessions().map { sessions ->
            sessions.sumOf { it.distanceMeters.toDouble() }.toFloat()
        }
    suspend fun clearAllSessions() {
        walkSessionDao.deleteAllSessions()
    }
}