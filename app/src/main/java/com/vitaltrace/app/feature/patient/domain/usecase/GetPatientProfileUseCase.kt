package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.PatientProfile
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetPatientProfileUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(): Result<PatientProfile> = repository.getProfile()
}
