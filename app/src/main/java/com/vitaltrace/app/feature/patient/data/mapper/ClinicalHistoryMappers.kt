package com.vitaltrace.app.feature.patient.data.mapper

import com.vitaltrace.app.feature.patient.data.dto.clinicalhistory.*
import com.vitaltrace.app.feature.patient.domain.model.*

fun ClinicalHistoryDto.toDomain() = ClinicalHistory(
    recordNumber,
    diagnoses.map { it.toDomain() },
    clinicalEvolutions.map { it.toDomain() },
    currentTreatments.map { it.toDomain() },
    recentMeasurements.map { it.toDomain() }
)

private fun ClinicalProfessionalDto.toDomain() =
    ClinicalProfessional(fullName, professionalType, specialty)

private fun ClinicalDiagnosisDto.toDomain() = ClinicalDiagnosis(
    id, cieCode, description, diagnosisDate, status, professional?.toDomain()
)

private fun ClinicalEvolutionDto.toDomain() = ClinicalEvolution(
    id, clinicalSummary, status, recordedAt, professional?.toDomain()
)

private fun ClinicalTreatmentDto.toDomain() = ClinicalTreatment(
    id, diagnosisId, indications, startDate, endDate, status, professional?.toDomain()
)

private fun ClinicalMeasurementDto.toDomain() = ClinicalMeasurement(
    id,
    value,
    unit,
    measuredAt,
    observation,
    measurementType?.let { ClinicalMeasurementType(it.id, it.name, it.code) }
)
