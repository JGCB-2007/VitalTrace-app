package com.vitaltrace.app.feature.auth.domain.usecase

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
            return Result.failure(AuthException("Enter a valid email address."))
        }
        if (token.isBlank()) return Result.failure(AuthException("Token is required."))
        if (password.length < 8 ||
            password.none(Char::isLetter) ||
            password.none(Char::isDigit) ||
            password.all(Char::isLetterOrDigit)
        ) {
            return Result.failure(
                AuthException("Password must contain at least 8 characters, letters, numbers and a symbol.")
            )
        }
        if (password != passwordConfirmation) {
            return Result.failure(AuthException("Passwords do not match."))
        }
        return repository.resetPassword(normalized, token.trim(), password, passwordConfirmation)
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
