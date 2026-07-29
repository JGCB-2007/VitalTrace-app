package com.vitaltrace.app.feature.patient.domain.model

data class Appointment(
    val id: Long,
    val scheduledAt: String,
    val durationMinutes: Int,
    val reason: String,
    val status: String,
    val professional: Professional?
)

data class Professional(
    val id: Long,
    val professionalType: String,
    val fullName: String?,
    val specialty: Specialty?
)

data class Specialty(
    val id: Long,
    val name: String
)
