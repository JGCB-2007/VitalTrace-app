package com.vitaltrace.app.feature.patient.domain.model

data class PatientRelative(
    val id: Long,
    val patientId: Long,
    val relativeId: Long,
    val relationship: String,
    val status: String,
    val startDate: String,
    val endDate: String?,
    val relative: Relative?
)

data class Relative(
    val id: Long,
    val personId: Long,
    val person: RelativePerson?
)

data class RelativePerson(
    val id: Long,
    val fullName: String?,
    val phone: String?
)
