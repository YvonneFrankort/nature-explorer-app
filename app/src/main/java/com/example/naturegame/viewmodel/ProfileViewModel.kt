package com.example.naturegame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.profile.ProfileRepository
import com.example.naturegame.data.remote.firebase.AuthManager
import com.example.naturegame.data.repository.WalkRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val authManager: AuthManager,
    private val walkRepository: WalkRepository,
) : ViewModel() {

    // -----------------------------
    // USER AUTH
    // -----------------------------
    private val _currentUser = MutableStateFlow<FirebaseUser?>(authManager.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    fun signInAnonymously() {
        viewModelScope.launch {
            val result = authManager.signInAnonymously()
            result.onSuccess {
                _currentUser.value = authManager.currentUser
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        _currentUser.value = null
    }

    // -----------------------------
    // PROFILE DATA
    // -----------------------------
    val profileName: StateFlow<String> =
        repository.profileName.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    val profilePictureUri: StateFlow<String> =
        repository.profilePictureUri.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ""
        )

    fun updateName(name: String) {
        viewModelScope.launch { repository.updateName(name) }
    }

    fun updatePicture(uri: String) {
        viewModelScope.launch { repository.updatePicture(uri) }
    }

    // -----------------------------
    // WALKING DATA
    // -----------------------------
    val totalSteps = walkRepository.getAllSessions()
        .map { sessions -> sessions.sumOf { it.stepCount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalDistance = walkRepository.getAllSessions()
        .map { sessions -> sessions.sumOf { it.distanceMeters.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val findingsCount: StateFlow<Int> =
        repository.findingsCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    // -----------------------------
    // SETTINGS: UNIT SWITCHING
    // -----------------------------
    private val _useMiles = MutableStateFlow(false)
    val useMiles: StateFlow<Boolean> = _useMiles

    fun toggleUnits() {
        _useMiles.value = !_useMiles.value
    }

    // -----------------------------
    // BADGES
    // -----------------------------
    data class Badge(
        val id: String,
        val label: String,
        val unlocked: Boolean
    )

    val badges: StateFlow<List<Badge>> =
        combine(totalSteps, findingsCount, totalDistance) { steps, findings, distance ->
            listOf(
                Badge("walker", "Walker", steps >= 1000),
                Badge("explorer", "Explorer", findings >= 5),
                Badge("tracker", "Tracker", distance >= 1000f)
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )


    // -----------------------------
    // RESET PROGRESS
    // -----------------------------
    fun resetAll() {
        viewModelScope.launch {
            // Reset DataStore values
            repository.updateName("Explorer")
            repository.updatePicture("")

            // Reset walking data (via WalkRepository)
            walkRepository.clearAllSessions()

        }
    }
}
