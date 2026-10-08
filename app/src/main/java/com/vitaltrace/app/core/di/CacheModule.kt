package com.vitaltrace.app.core.di

import android.content.Context
import androidx.room.Room
import com.vitaltrace.app.core.cache.PatientSnapshotDao
import com.vitaltrace.app.core.cache.PatientSnapshotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {
    @Provides
    @Singleton
    fun providePatientSnapshotDatabase(
        @ApplicationContext context: Context
    ): PatientSnapshotDatabase = Room.databaseBuilder(
        context,
        PatientSnapshotDatabase::class.java,
        "vitaltrace_patient_cache.db"
    ).build()

    @Provides
    fun providePatientSnapshotDao(database: PatientSnapshotDatabase): PatientSnapshotDao =
        database.snapshots()
}
