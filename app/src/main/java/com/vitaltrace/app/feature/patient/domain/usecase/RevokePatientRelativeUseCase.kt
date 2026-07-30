package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class RevokePatientRelativeUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(id: Long): Result<PatientRelative> = repository.revokeRelative(id)
}
