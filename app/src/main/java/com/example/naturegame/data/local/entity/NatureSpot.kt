package com.example.naturegame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "nature_spots")
data class NatureSpot(
    @PrimaryKey val id: String,

    val name: String? = null,
    val latitude: Double,
    val longitude: Double,

    val imageLocalPath: String? = null,
    val imageFirebaseUrl: String? = null,

    val plantLabel: String? = null,
    val confidence: Float? = null,

    val note: String? = null,

    val userId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),

    val synced: Boolean = false,

)
