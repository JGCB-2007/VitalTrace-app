package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class AuthorizePatientRelativeUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(id: Long): Result<PatientRelative> = repository.authorizeRelative(id)
}
