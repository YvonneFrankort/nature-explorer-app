package com.example.naturegame.di

import android.content.Context
import com.example.naturegame.data.profile.ProfileRepository
import com.example.naturegame.data.repository.NatureSpotRepository
import com.example.naturegame.data.local.dao.NatureSpotDao
import com.example.naturegame.data.remote.firebase.AuthManager
import com.example.naturegame.data.remote.firebase.FirestoreManager
import com.example.naturegame.data.remote.firebase.StorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        @ApplicationContext context: Context,
        dao: NatureSpotDao
    ): ProfileRepository = ProfileRepository(context, dao)

    @Provides
    @Singleton
    fun provideNatureSpotRepository(
        dao: NatureSpotDao,
        firestoreManager: FirestoreManager,
        storageManager: StorageManager,
        authManager: AuthManager
    ): NatureSpotRepository = NatureSpotRepository(
        dao,
        firestoreManager,
        storageManager,
        authManager
    )
    @Provides
    @Singleton
    fun provideAuthManager(): AuthManager = AuthManager()

    @Provides
    @Singleton
    fun provideFirestoreManager(): FirestoreManager = FirestoreManager()

    @Provides
    @Singleton
    fun provideStorageManager(): StorageManager = StorageManager()
}
