package com.vitaltrace.app.feature.relativeportal.di

import com.vitaltrace.app.feature.relativeportal.data.remote.RelativePortalApiService
import com.vitaltrace.app.feature.relativeportal.data.repository.RelativeRepositoryImpl
import com.vitaltrace.app.feature.relativeportal.domain.repository.RelativeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RelativePortalModule {
    @Binds
    @Singleton
    abstract fun bindRelativeRepository(implementation: RelativeRepositoryImpl): RelativeRepository

    companion object {
        @Provides
        @Singleton
        fun provideRelativePortalApiService(retrofit: Retrofit): RelativePortalApiService =
            retrofit.create(RelativePortalApiService::class.java)
    }
}