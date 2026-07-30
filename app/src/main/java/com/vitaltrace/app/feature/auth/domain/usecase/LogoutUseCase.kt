package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.core.session.SessionManager
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(): Result<Unit> {
        return sessionManager.logout()
    }
}
