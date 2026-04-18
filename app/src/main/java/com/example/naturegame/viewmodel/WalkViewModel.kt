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

    private var stepsAtStart: Int? = null
    private var stepsDuringWalk: Int = 0

    fun startWalk() {
        if (_isWalking.value) return

        val session = WalkSession()
        _currentSession.value = session
        _isWalking.value = true

        // Save only the start of the session
        viewModelScope.launch {
            db.walkSessionDao().insert(session)
        }

        stepManager.startStepCounting { totalSteps ->

            if (stepsAtStart == null) {
                stepsAtStart = totalSteps
            }

            val start = stepsAtStart ?: return@startStepCounting
            val stepsDuringWalk = totalSteps - start

            viewModelScope.launch {
                val current = _currentSession.value ?: return@launch

                _currentSession.value = current.copy(
                    stepCount = stepsDuringWalk,
                    distanceMeters = stepsDuringWalk * StepCounterManager.STEP_LENGTH_METERS
                )
            }
        }
    }

    fun stopWalk(onWalkFinished: (WalkSession) -> Unit) {
        stepManager.stopStepCounting()
        _isWalking.value = false

        stepsAtStart = null

        _currentSession.update { it?.copy(
            endTime = System.currentTimeMillis(),
            isActive = false
        )}

        // Save final session only once
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
