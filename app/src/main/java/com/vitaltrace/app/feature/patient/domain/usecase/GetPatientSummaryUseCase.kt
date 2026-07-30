package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetPatientSummaryUseCase @Inject constructor(
    private val patientRepository: PatientRepository
) {
    suspend operator fun invoke(): Result<PatientSummary> {
        return patientRepository.getSummary()
    }
}
