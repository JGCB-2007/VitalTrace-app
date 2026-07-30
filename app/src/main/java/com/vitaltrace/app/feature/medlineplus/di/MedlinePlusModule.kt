package com.vitaltrace.app.feature.medlineplus.di

import com.vitaltrace.app.feature.medlineplus.data.remote.MedlinePlusApiService
import com.vitaltrace.app.feature.medlineplus.data.repository.MedlinePlusRepositoryImpl
import com.vitaltrace.app.feature.medlineplus.domain.repository.MedlinePlusRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.inject.Qualifier
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MedlinePlusRetrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class MedlinePlusModule {
    @Binds
    @Singleton
    abstract fun bindMedlinePlusRepository(implementation: MedlinePlusRepositoryImpl): MedlinePlusRepository

    companion object {
        @Provides
        @Singleton
        @MedlinePlusRetrofit
        fun provideMedlinePlusRetrofit(json: Json): Retrofit = Retrofit.Builder()
            .baseUrl("https://connect.medlineplus.gov/")
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .callTimeout(12, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        @Provides
        @Singleton
        fun provideMedlinePlusApiService(
            @MedlinePlusRetrofit retrofit: Retrofit
        ): MedlinePlusApiService = retrofit.create(MedlinePlusApiService::class.java)
    }
}
