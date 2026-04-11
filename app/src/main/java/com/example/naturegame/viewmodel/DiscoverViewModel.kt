package com.example.naturegame.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturegame.data.local.AppDatabase
import com.example.naturegame.data.remote.firebase.AuthManager
import com.example.naturegame.data.remote.firebase.FirestoreManager
import com.example.naturegame.data.remote.firebase.StorageManager
import com.example.naturegame.data.repository.NatureSpotRepository
import com.example.naturegame.data.local.entity.NatureSpot
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NatureSpotRepository by lazy {
        NatureSpotRepository(
            dao = AppDatabase.getDatabase(application).natureSpotDao(),
            firestoreManager = FirestoreManager(),
            storageManager = StorageManager(),
            authManager = AuthManager()
        )
    }

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
