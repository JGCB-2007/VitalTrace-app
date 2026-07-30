package com.vitaltrace.app.feature.patient.domain.model

data class PatientProfile(
    val userId: Long,
    val patientId: Long,
    val recordNumber: String,
    val fullName: String?,
    val email: String,
    val phone: String?,
    val dateOfBirth: String?,
    val age: Int?,
    val gender: String?,
    val address: String?,
    val identificationNumber: String?,
    val emergencyContact: EmergencyContact?,
    val accountStatus: String,
    val administrativeStatus: String
)

data class EmergencyContact(
    val name: String?,
    val phone: String?
)
