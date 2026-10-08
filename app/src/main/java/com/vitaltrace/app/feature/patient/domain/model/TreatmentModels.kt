package com.vitaltrace.app.feature.patient.domain.model

import kotlinx.serialization.Serializable

@Serializable
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

@Serializable data class Diagnosis(
    val id: Long,
    val cieCode: String?,
    val description: String,
    val diagnosisDate: String,
    val status: String
)

@Serializable data class Prescriber(
    val id: Long,
    val professionalType: String?,
    val fullName: String?,
    val specialty: Specialty?
)
