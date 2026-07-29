package com.vitaltrace.app.feature.patient.di

import com.vitaltrace.app.feature.patient.data.remote.PatientPortalApiService
import com.vitaltrace.app.feature.patient.data.repository.PatientRepositoryImpl
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PatientModule {
    @Binds
    @Singleton
    abstract fun bindPatientRepository(
        implementation: PatientRepositoryImpl
    ): PatientRepository

    companion object {
        @Provides
        @Singleton
        fun providePatientPortalApiService(retrofit: Retrofit): PatientPortalApiService =
            retrofit.create(PatientPortalApiService::class.java)
    }
}
