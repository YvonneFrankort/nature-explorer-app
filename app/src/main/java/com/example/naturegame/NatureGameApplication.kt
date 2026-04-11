package com.example.naturegame

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class NatureGameApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        Configuration.getInstance().userAgentValue = packageName
    }
}
