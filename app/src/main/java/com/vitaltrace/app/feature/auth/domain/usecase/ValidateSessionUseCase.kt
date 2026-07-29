package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return authRepository.validateSession()
    }
}