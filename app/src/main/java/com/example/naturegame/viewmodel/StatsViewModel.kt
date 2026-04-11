package com.example.naturegame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.AppDatabase
import com.example.naturegame.data.repository.WalkRepository
import com.example.naturegame.data.local.entity.WalkSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WalkRepository(db.walkSessionDao())

    private val _sessions = MutableStateFlow<List<WalkSession>>(emptyList())
    val sessions: StateFlow<List<WalkSession>> = _sessions.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            repository.getAllSessions().collect { list ->
                _sessions.value = list
            }
        }
    }

    fun totalSteps(): Int =
        _sessions.value.sumOf { it.stepCount }

    fun totalDistance(): Float =
        _sessions.value.sumOf { it.distanceMeters.toDouble() }.toFloat()

    fun totalWalks(): Int =
        _sessions.value.size
}