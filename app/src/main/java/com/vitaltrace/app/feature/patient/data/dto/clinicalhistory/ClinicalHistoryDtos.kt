package com.vitaltrace.app.feature.patient.data.dto.clinicalhistory

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClinicalHistoryDto(
    @SerialName("record_number") val recordNumber: String,
    val diagnoses: List<ClinicalDiagnosisDto> = emptyList(),
    @SerialName("clinical_evolutions")
    val clinicalEvolutions: List<ClinicalEvolutionDto> = emptyList(),
    @SerialName("current_treatments")
    val currentTreatments: List<ClinicalTreatmentDto> = emptyList(),
    @SerialName("recent_measurements")
    val recentMeasurements: List<ClinicalMeasurementDto> = emptyList()
)

@Serializable
data class ClinicalProfessionalDto(
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("professional_type") val professionalType: String? = null,
    val specialty: String? = null
)

@Serializable
data class ClinicalDiagnosisDto(
    val id: Long,
    @SerialName("cie_code") val cieCode: String? = null,
    val description: String,
    @SerialName("diagnosis_date") val diagnosisDate: String,
    val status: String,
    val professional: ClinicalProfessionalDto? = null
)

@Serializable
data class ClinicalEvolutionDto(
    val id: Long,
    @SerialName("clinical_summary") val clinicalSummary: String,
    val status: String,
    @SerialName("recorded_at") val recordedAt: String,
    val professional: ClinicalProfessionalDto? = null
)

@Serializable
data class ClinicalTreatmentDto(
    val id: Long,
    @SerialName("diagnosis_id") val diagnosisId: Long? = null,
    val indications: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String? = null,
    val status: String,
    val professional: ClinicalProfessionalDto? = null
)

@Serializable
data class ClinicalMeasurementDto(
    val id: Long,
    val value: Double,
    val unit: String,
    @SerialName("measured_at") val measuredAt: String,
    val observation: String? = null,
    @SerialName("measurement_type")
    val measurementType: ClinicalMeasurementTypeDto? = null
)

@Serializable
data class ClinicalMeasurementTypeDto(
    val id: Long,
    val name: String,
    val code: String
)
