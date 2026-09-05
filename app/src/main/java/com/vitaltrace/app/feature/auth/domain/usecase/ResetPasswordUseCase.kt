package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.core.presentation.localization.AuthMessagesEs
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        token: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> {
        val normalized = email.trim().lowercase()
        if (normalized.isBlank() || !EMAIL_REGEX.matches(normalized)) {
            return Result.failure(AuthException(AuthMessagesEs.EMAIL_INVALID))
        }
        if (token.isBlank()) return Result.failure(AuthException(AuthMessagesEs.TOKEN_REQUIRED))
        if (password.length < 8 ||
            password.none(Char::isLetter) ||
            password.none(Char::isDigit) ||
            password.all(Char::isLetterOrDigit)
        ) {
            return Result.failure(AuthException(AuthMessagesEs.PASSWORD_POLICY))
        }
        if (password != passwordConfirmation) {
            return Result.failure(AuthException(AuthMessagesEs.PASSWORDS_MISMATCH))
        }
        return repository.resetPassword(normalized, token.trim(), password, passwordConfirmation)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
