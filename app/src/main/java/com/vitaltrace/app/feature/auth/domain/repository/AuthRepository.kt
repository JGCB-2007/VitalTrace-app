package com.vitaltrace.app.feature.auth.domain.repository

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun validateSession(): Result<Unit>

    suspend fun logout(): Result<Unit>
}