package com.example.naturegame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.AppDatabase
import com.example.naturegame.data.local.entity.WalkSession
import com.example.naturegame.sensor.StepCounterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalkViewModel(application: Application) : AndroidViewModel(application) {

    private val stepManager = StepCounterManager(application)
    private val db = AppDatabase.getDatabase(application)

    private val _currentSession = MutableStateFlow<WalkSession?>(null)
    val currentSession: StateFlow<WalkSession?> = _currentSession.asStateFlow()

    private val _isWalking = MutableStateFlow(false)
    val isWalking: StateFlow<Boolean> = _isWalking.asStateFlow()

    // NEW: store the starting step count
    private var stepsAtStart: Int? = null

    fun startWalk() {
        if (_isWalking.value) return

        val session = WalkSession()
        _currentSession.value = session
        _isWalking.value = true

        // Insert session immediately
        viewModelScope.launch {
            db.walkSessionDao().insert(session)
        }

        // Start step counting with total step values
        stepManager.startStepCounting { totalSteps ->

            // First reading becomes the baseline
            if (stepsAtStart == null) {
                stepsAtStart = totalSteps
            }

            val start = stepsAtStart ?: totalSteps
            val stepsDuringWalk = totalSteps - start

            _currentSession.update { current ->
                current?.copy(
                    stepCount = stepsDuringWalk,
                    distanceMeters = stepsDuringWalk * StepCounterManager.STEP_LENGTH_METERS
                )
            }

            // Save updates to DB
            viewModelScope.launch {
                _currentSession.value?.let { updated ->
                    db.walkSessionDao().update(updated)
                }
            }
        }
    }

    fun stopWalk(onWalkFinished: (WalkSession) -> Unit) {
        stepManager.stopStepCounting()
        _isWalking.value = false

        // Reset baseline for next walk
        stepsAtStart = null

        _currentSession.update { it?.copy(
            endTime = System.currentTimeMillis(),
            isActive = false
        )}

        viewModelScope.launch {
            _currentSession.value?.let { session ->
                db.walkSessionDao().update(session)
                onWalkFinished(session)
            }
            _currentSession.value = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepManager.stopAll()
    }
}

fun formatDistance(meters: Float): String {
    return if (meters < 1000f) {
        "${meters.toInt()} m"
    } else {
        "${"%.1f".format(meters / 1000f)} km"
    }
}

fun formatDuration(startTime: Long, endTime: Long = System.currentTimeMillis()): String {
    val seconds = (endTime - startTime) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60

    return when {
        hours > 0 -> "${hours}h ${minutes % 60}min"
        minutes > 0 -> "${minutes}min ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
