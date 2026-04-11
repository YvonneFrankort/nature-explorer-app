package com.example.naturegame.data.repository

import com.example.naturegame.data.local.dao.WalkSessionDao
import com.example.naturegame.data.local.entity.WalkSession
import kotlinx.coroutines.flow.Flow

class WalkRepository(
    private val walkSessionDao: WalkSessionDao
) {

    fun getAllSessions(): Flow<List<WalkSession>> =
        walkSessionDao.getAllSessions()

    suspend fun insertSession(session: WalkSession) {
        walkSessionDao.insert(session)
    }
}