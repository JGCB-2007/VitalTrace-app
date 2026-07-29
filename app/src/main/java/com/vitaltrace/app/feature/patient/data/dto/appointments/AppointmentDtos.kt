package com.vitaltrace.app.feature.patient.data.dto.appointments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    val id: Long,
    @SerialName("scheduled_at") val scheduledAt: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    val reason: String,
    val status: String,
    val professional: ProfessionalDto? = null
)

@Serializable
data class ProfessionalDto(
    val id: Long,
    @SerialName("professional_type") val professionalType: String,
    @SerialName("full_name") val fullName: String? = null,
    val specialty: SpecialtyDto? = null
)

@Serializable
data class SpecialtyDto(
    val id: Long,
    val name: String
)
