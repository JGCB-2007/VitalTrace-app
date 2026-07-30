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
    val firstName: String?,
    val middleName: String?,
    val firstLastName: String?,
    val secondLastName: String?,
    val phone: String?
) {
    val fullName: String?
        get() = listOf(firstName, middleName, firstLastName, secondLastName)
            .mapNotNull { it?.trim()?.takeIf(String::isNotEmpty) }
            .joinToString(" ")
            .takeIf(String::isNotEmpty)
}
