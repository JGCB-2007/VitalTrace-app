package com.vitaltrace.app.feature.patient.data.dto.relatives

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientRelativeDto(
    val id: Long,
    @SerialName("patient_id") val patientId: Long,
    @SerialName("relative_id") val relativeId: Long,
    val relationship: String,
    val status: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String? = null,
    val relative: RelativeDto? = null
)

@Serializable
data class RelativeDto(
    val id: Long,
    @SerialName("person_id") val personId: Long,
    val person: RelativePersonDto? = null
)

@Serializable
data class RelativePersonDto(
    val id: Long,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("middle_name") val middleName: String? = null,
    @SerialName("first_last_name") val firstLastName: String? = null,
    @SerialName("second_last_name") val secondLastName: String? = null,
    val phone: String? = null
)
