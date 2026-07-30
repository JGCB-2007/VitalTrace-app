package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetPatientRelativesUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(page: Int? = null): Result<Page<PatientRelative>> =
        repository.getRelatives(page)
}
