package com.vitaltrace.app.feature.nurseportal.di

import com.vitaltrace.app.feature.nurseportal.data.remote.NursePortalApiService
import com.vitaltrace.app.feature.nurseportal.data.repository.NurseRepositoryImpl
import com.vitaltrace.app.feature.nurseportal.domain.repository.NurseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NursePortalModule {
    @Binds @Singleton abstract fun bindNurseRepository(implementation: NurseRepositoryImpl): NurseRepository
    companion object {
        @Provides @Singleton
        fun provideNursePortalApiService(retrofit: Retrofit): NursePortalApiService = retrofit.create(NursePortalApiService::class.java)
    }
}