package com.vitaltrace.app.core.di

import com.vitaltrace.app.core.datastore.TokenDataStore
import com.vitaltrace.app.core.session.TokenStore
import com.vitaltrace.app.feature.auth.data.repository.AuthRepositoryImpl
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenStore(
        implementation: TokenDataStore
    ): TokenStore
}
