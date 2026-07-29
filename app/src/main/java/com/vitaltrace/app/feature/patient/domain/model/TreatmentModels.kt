package com.vitaltrace.app.feature.patient.domain.model

data class Treatment(
    val id: Long,
    val diagnosisId: Long?,
    val diagnosis: Diagnosis?,
    val indications: String,
    val startDate: String,
    val endDate: String?,
    val status: String,
    val prescribedBy: Long,
    val prescriber: Prescriber?
)

data class Diagnosis(
    val id: Long,
    val cieCode: String?,
    val description: String,
    val diagnosisDate: String,
    val status: String
)

data class Prescriber(
    val id: Long,
    val professionalType: String?,
    val fullName: String?,
    val specialty: Specialty?
)
