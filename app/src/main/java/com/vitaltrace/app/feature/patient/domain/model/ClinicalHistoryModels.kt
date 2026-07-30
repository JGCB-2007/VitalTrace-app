package com.vitaltrace.app.feature.patient.domain.model

data class ClinicalHistory(
    val recordNumber: String,
    val diagnoses: List<ClinicalDiagnosis>,
    val clinicalEvolutions: List<ClinicalEvolution>,
    val currentTreatments: List<ClinicalTreatment>,
    val recentMeasurements: List<ClinicalMeasurement>
)

data class ClinicalProfessional(
    val fullName: String?,
    val professionalType: String?,
    val specialty: String?
)

data class ClinicalDiagnosis(
    val id: Long,
    val cieCode: String?,
    val description: String,
    val diagnosisDate: String,
    val status: String,
    val professional: ClinicalProfessional?
)

data class ClinicalEvolution(
    val id: Long,
    val clinicalSummary: String,
    val status: String,
    val recordedAt: String,
    val professional: ClinicalProfessional?
)

data class ClinicalTreatment(
    val id: Long,
    val diagnosisId: Long?,
    val indications: String,
    val startDate: String,
    val endDate: String?,
    val status: String,
    val professional: ClinicalProfessional?
)

data class ClinicalMeasurement(
    val id: Long,
    val value: Double,
    val unit: String,
    val measuredAt: String,
    val observation: String?,
    val measurementType: ClinicalMeasurementType?
)

data class ClinicalMeasurementType(val id: Long, val name: String, val code: String)
