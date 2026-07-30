package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetUnreadNotificationsCountUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(): Result<Int> = repository.getUnreadNotificationsCount()
}
