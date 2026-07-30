package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val normalized = email.trim().lowercase()
        if (normalized.isBlank()) return Result.failure(AuthException("Email is required."))
        if (!EMAIL_REGEX.matches(normalized)) {
            return Result.failure(AuthException("Enter a valid email address."))
        }
        return repository.forgotPassword(normalized)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
