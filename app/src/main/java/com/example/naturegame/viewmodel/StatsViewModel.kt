package com.example.naturegame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.AppDatabase
import com.example.naturegame.data.repository.WalkRepository
import com.example.naturegame.data.local.entity.WalkSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class StatsViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WalkRepository(db.walkSessionDao())

    // All sessions as a StateFlow
    val sessions: StateFlow<List<WalkSession>> =
        repository.getAllSessions()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    // ⭐ Reactive summary values (THIS FIXES YOUR BUG)
    val totalSteps: StateFlow<Int> =
        sessions.map { list ->
            list.sumOf { it.stepCount }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val totalDistance: StateFlow<Float> =
        sessions.map { list ->
            list.sumOf { it.distanceMeters.toDouble() }.toFloat()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0f
        )

    val totalWalks: StateFlow<Int> =
        sessions.map { list ->
            list.size
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )
}
