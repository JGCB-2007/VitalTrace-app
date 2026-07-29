package com.vitaltrace.app.feature.patient.data.dto.summary

import com.vitaltrace.app.feature.patient.data.dto.appointments.AppointmentDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.MeasurementDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.TreatmentDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientSummaryDataDto(
    val patient: PatientSummaryDto,
    @SerialName("next_appointment") val nextAppointment: AppointmentDto? = null,
    @SerialName("latest_measurements") val latestMeasurements: List<MeasurementDto> = emptyList(),
    @SerialName("active_treatments") val activeTreatments: List<TreatmentDto> = emptyList(),
    @SerialName("alerts_summary") val alertsSummary: AlertsSummaryDto
)

@Serializable
data class PatientSummaryDto(
    val id: Long,
    @SerialName("record_number") val recordNumber: String,
    @SerialName("administrative_status") val administrativeStatus: String,
    @SerialName("full_name") val fullName: String? = null
)

@Serializable
data class AlertsSummaryDto(
    val open: Int,
    val critical: Int
)
