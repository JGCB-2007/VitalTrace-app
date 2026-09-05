package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.core.presentation.localization.AuthMessagesEs
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        val normalizedEmail = email.trim().lowercase()

        if (normalizedEmail.isBlank()) {
            return Result.failure(
                AuthException(AuthMessagesEs.EMAIL_REQUIRED)
            )
        }

        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            return Result.failure(
                AuthException(AuthMessagesEs.EMAIL_INVALID)
            )
        }

        if (password.isBlank()) {
            return Result.failure(
                AuthException(AuthMessagesEs.PASSWORD_REQUIRED)
            )
        }

        return sessionManager.login(
            email = normalizedEmail,
            password = password
        )
    }

    private companion object {
        val EMAIL_REGEX =
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}
