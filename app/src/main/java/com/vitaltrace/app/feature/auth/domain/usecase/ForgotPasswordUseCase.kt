package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.core.presentation.localization.AuthMessagesEs
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val normalized = email.trim().lowercase()
        if (normalized.isBlank()) return Result.failure(AuthException(AuthMessagesEs.EMAIL_REQUIRED))
        if (!EMAIL_REGEX.matches(normalized)) {
            return Result.failure(AuthException(AuthMessagesEs.EMAIL_INVALID))
        }
        return repository.forgotPassword(normalized)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
