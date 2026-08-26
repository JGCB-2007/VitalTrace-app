package com.vitaltrace.app.feature.auth.domain.repository

import com.vitaltrace.app.core.session.AuthenticatedUser

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthenticatedUser>

    suspend fun getCurrentUser(): Result<AuthenticatedUser>

    suspend fun logout(): Result<Unit>

    suspend fun forgotPassword(email: String): Result<Unit>

    suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit>

    suspend fun resendActivationCode(email: String): Result<Unit>

    suspend fun activateAccount(
        email: String,
        code: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit>
}