package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetPatientMeasurementsUseCase @Inject constructor(
    private val patientRepository: PatientRepository
) {
    suspend operator fun invoke(page: Int? = null, forceRefresh: Boolean = false): Result<Page<Measurement>> {
        return patientRepository.getMeasurements(page = page, forceRefresh = forceRefresh)
    }
}
