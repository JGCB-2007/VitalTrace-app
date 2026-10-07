package com.vitaltrace.app.feature.patient.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Appointment(
    val id: Long,
    val scheduledAt: String,
    val durationMinutes: Int,
    val reason: String,
    val status: String,
    val professional: Professional?
)

@Serializable data class Professional(
    val id: Long,
    val professionalType: String,
    val fullName: String?,
    val specialty: Specialty?
)

@Serializable data class Specialty(
    val id: Long,
    val name: String
)
