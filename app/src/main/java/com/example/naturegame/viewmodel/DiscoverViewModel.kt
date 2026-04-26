package com.example.naturegame.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.data.remote.firebase.AuthManager
import com.example.naturegame.data.repository.NatureSpotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: NatureSpotRepository,
    private val authManager: AuthManager
) : ViewModel() {

    val allSpots = repository.allSpots.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun deleteSpot(spot: NatureSpot) {
        viewModelScope.launch {
            repository.deleteSpot(spot)
        }
    }
}
