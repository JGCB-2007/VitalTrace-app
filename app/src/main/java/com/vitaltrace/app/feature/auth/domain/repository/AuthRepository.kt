package com.vitaltrace.app.feature.auth.domain.repository

import com.vitaltrace.app.core.session.AuthenticatedUser

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthenticatedUser>

    suspend fun getCurrentUser(): Result<AuthenticatedUser>

    suspend fun logout(): Result<Unit>
}
