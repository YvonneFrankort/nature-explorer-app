package com.example.naturegame.data.profile

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.naturegame.data.local.dao.NatureSpotDao
import com.example.naturegame.data.profile.profileDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val context: Context,
    private val natureSpotDao: NatureSpotDao
) {

    private object Keys {
        val NAME = stringPreferencesKey("profile_name")
        val PICTURE = stringPreferencesKey("profile_picture_uri")
        val STEPS = intPreferencesKey("profile_steps")
    }

    // READ FLOWS
    val profileName: Flow<String> = context.profileDataStore.data.map { prefs ->
        prefs[Keys.NAME] ?: "Anonymous user"
    }

    val profilePictureUri: Flow<String> = context.profileDataStore.data.map { prefs ->
        prefs[Keys.PICTURE] ?: ""
    }

    val profileSteps: Flow<Int> = context.profileDataStore.data.map { prefs ->
        prefs[Keys.STEPS] ?: 0
    }

    // NEW: Read discovery count from the database
    val findingsCount: Flow<Int> = natureSpotDao.getNatureSpotCount()

    // WRITE FUNCTIONS
    suspend fun updateName(name: String) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.NAME] = name
        }
    }

    suspend fun updatePicture(uri: String) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.PICTURE] = uri
        }
    }

    suspend fun updateSteps(steps: Int) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.STEPS] = steps
        }
    }
}
