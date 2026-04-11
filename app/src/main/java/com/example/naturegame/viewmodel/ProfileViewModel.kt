package com.example.naturegame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.profile.ProfileRepository
import com.example.naturegame.data.remote.firebase.AuthManager
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val authManager: AuthManager
) : ViewModel() {

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

    val profileSteps: StateFlow<Int> =
        repository.profileSteps.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    val findingsCount: StateFlow<Int> =
        repository.findingsCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    fun updateName(name: String) {
        viewModelScope.launch {
            repository.updateName(name)
        }
    }

    fun updatePicture(uri: String) {
        viewModelScope.launch {
            repository.updatePicture(uri)
        }
    }

    fun updateSteps(steps: Int) {
        viewModelScope.launch {
            repository.updateSteps(steps)
        }
    }
}
