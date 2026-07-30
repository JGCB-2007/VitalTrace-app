package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class CreatePatientMeasurementUseCase @Inject constructor(
    private val patientRepository: PatientRepository
) {
    suspend operator fun invoke(
        measurementTypeId: Long,
        value: Double,
        unit: String,
        measuredAt: String,
        observation: String?
    ): Result<Measurement> {
        return patientRepository.createMeasurement(
            measurementTypeId = measurementTypeId,
            value = value,
            unit = unit,
            measuredAt = measuredAt,
            observation = observation
        )
    }
}
