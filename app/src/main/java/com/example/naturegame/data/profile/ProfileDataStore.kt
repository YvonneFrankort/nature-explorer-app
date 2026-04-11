package com.example.naturegame.data.profile

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.profileDataStore by preferencesDataStore(name = "profile_prefs")
