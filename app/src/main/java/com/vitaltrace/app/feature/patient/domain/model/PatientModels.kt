package com.vitaltrace.app.feature.patient.domain.model

data class PatientSummary(
    val patient: SummaryPatient,
    val nextAppointment: Appointment?,
    val latestMeasurements: List<Measurement>,
    val activeTreatments: List<Treatment>,
    val alerts: AlertsSummary
)

data class SummaryPatient(
    val id: Long,
    val recordNumber: String,
    val administrativeStatus: String,
    val fullName: String?
)

data class AlertsSummary(
    val open: Int,
    val critical: Int
)
