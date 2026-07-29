package com.vitaltrace.app.feature.patient.data.dto.treatments

import com.vitaltrace.app.feature.patient.data.dto.appointments.SpecialtyDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TreatmentDto(
    val id: Long,
    @SerialName("diagnosis_id") val diagnosisId: Long? = null,
    val diagnosis: DiagnosisDto? = null,
    val indications: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String? = null,
    val status: String,
    @SerialName("prescribed_by") val prescribedBy: Long,
    val prescriber: PrescriberDto? = null
)

@Serializable
data class DiagnosisDto(
    val id: Long,
    @SerialName("cie_code") val cieCode: String? = null,
    val description: String,
    @SerialName("diagnosis_date") val diagnosisDate: String,
    val status: String
)

@Serializable
data class PrescriberDto(
    val id: Long,
    @SerialName("professional_type") val professionalType: String? = null,
    @SerialName("full_name") val fullName: String? = null,
    val specialty: SpecialtyDto? = null
)
