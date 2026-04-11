package com.example.naturegame.di

import android.content.Context
import androidx.room.Room
import com.example.naturegame.data.local.AppDatabase
import com.example.naturegame.data.local.dao.NatureSpotDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "luontopeli_database"   // <-- FIXED
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideNatureSpotDao(db: AppDatabase): NatureSpotDao =
        db.natureSpotDao()
}
