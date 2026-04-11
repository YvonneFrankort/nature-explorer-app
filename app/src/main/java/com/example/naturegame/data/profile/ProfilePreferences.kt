package com.example.luontopeli.data.profile

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

object ProfilePreferences {
    val PROFILE_NAME = stringPreferencesKey("profile_name")
    val PROFILE_PICTURE_URI = stringPreferencesKey("profile_picture_uri")
    val PROFILE_STEPS = intPreferencesKey("profile_steps")
}
