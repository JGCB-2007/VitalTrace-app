package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetClinicalHistoryUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(): Result<ClinicalHistory> = repository.getClinicalHistory()
}
