package com.vitaltrace.app.feature.patient.data.dto.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientProfileDto(
    @SerialName("user_id") val userId: Long,
    @SerialName("patient_id") val patientId: Long,
    @SerialName("record_number") val recordNumber: String,
    @SerialName("full_name") val fullName: String? = null,
    val email: String,
    val phone: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val address: String? = null,
    @SerialName("identification_number") val identificationNumber: String? = null,
    @SerialName("emergency_contact") val emergencyContact: EmergencyContactDto? = null,
    @SerialName("account_status") val accountStatus: String,
    @SerialName("administrative_status") val administrativeStatus: String
)

@Serializable
data class EmergencyContactDto(
    val name: String? = null,
    val phone: String? = null
)
