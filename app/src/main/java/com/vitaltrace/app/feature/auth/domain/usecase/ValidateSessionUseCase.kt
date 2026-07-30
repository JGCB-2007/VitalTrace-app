package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.SessionState
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import javax.inject.Inject

class ValidateSessionUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(): Result<Unit> {
        sessionManager.restoreSession()
        return when (val state = sessionManager.state.value) {
            is SessionState.Authenticated -> Result.success(Unit)
            is SessionState.Error -> Result.failure(AuthException(state.message))
            SessionState.Loading -> Result.failure(AuthException("Session validation did not finish."))
            SessionState.Unauthenticated -> Result.failure(AuthException("No active session was found."))
        }
    }
}
