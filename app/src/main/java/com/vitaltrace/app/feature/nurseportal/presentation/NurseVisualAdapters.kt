package com.vitaltrace.app.feature.nurseportal.presentation

import com.vitaltrace.app.core.presentation.localization.EnumDisplayEs
import com.vitaltrace.app.core.presentation.localization.SpanishDateTime
import com.vitaltrace.app.feature.appointments.presentation.AppointmentDetailUiModel
import com.vitaltrace.app.feature.appointments.presentation.AppointmentStatus
import com.vitaltrace.app.feature.appointments.presentation.AppointmentUiModel
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAppointment
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseHistory
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseMeasurement
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseTreatment
import com.vitaltrace.app.feature.patient.domain.model.ClinicalDiagnosis
import com.vitaltrace.app.feature.patient.domain.model.ClinicalEvolution
import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import com.vitaltrace.app.feature.patient.domain.model.ClinicalMeasurement
import com.vitaltrace.app.feature.patient.domain.model.ClinicalMeasurementType
import com.vitaltrace.app.feature.patient.domain.model.ClinicalTreatment
import com.vitaltrace.app.feature.patient.domain.model.Diagnosis
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.MeasurementType
import com.vitaltrace.app.feature.patient.domain.model.Treatment

internal fun NurseMeasurement.toPatientMeasurement() = Measurement(
    id = id,
    patientId = patientId,
    measurementTypeId = typeId ?: 0,
    value = value,
    unit = unit,
    measuredAt = measuredAt,
    origin = origin,
    authorUserId = 0,
    observation = observation,
    reviewStatus = reviewStatus ?: "PENDING",
    reviewedAt = null,
    reviewedBy = null,
    reviewObservation = null,
    reviewer = null,
    measurementType = typeId?.let {
        MeasurementType(it, typeName.orEmpty(), baseUnit ?: unit, 0, true)
    }
)

internal fun NurseTreatment.toPatientTreatment() = Treatment(
    id = id,
    diagnosisId = diagnosis?.id,
    diagnosis = diagnosis?.let { Diagnosis(it.id, it.cieCode, it.description, it.date, it.status) },
    indications = indications,
    startDate = startDate,
    endDate = endDate,
    status = status,
    prescribedBy = 0,
    prescriber = null
)

internal fun NurseHistory.toPatientClinicalHistory() = ClinicalHistory(
    recordNumber = patient.recordNumber,
    diagnoses = diagnoses.map { ClinicalDiagnosis(it.id, it.cieCode, it.description, it.date, it.status, null) },
    clinicalEvolutions = evolutions.map { ClinicalEvolution(it.id, it.summary, it.status, it.recordedAt, null) },
    currentTreatments = treatments.map {
        ClinicalTreatment(it.id, it.diagnosis?.id, it.indications, it.startDate, it.endDate, it.status, null)
    },
    recentMeasurements = measurements.map {
        ClinicalMeasurement(
            id = it.id,
            value = it.value.toDoubleOrNull() ?: 0.0,
            unit = it.unit,
            measuredAt = it.measuredAt,
            observation = it.observation,
            measurementType = it.typeId?.let { id -> ClinicalMeasurementType(id, it.typeName.orEmpty(), null) }
        )
    }
)

internal fun NurseAppointment.toAppointmentUiModel(patientName: String): AppointmentUiModel {
    val (displayDate, displayTime) = SpanishDateTime.formatApiDateTime(scheduledAt)
    return AppointmentUiModel(
        id = id,
        professionalName = patientName,
        professionalInitials = initials(patientName),
        specialty = professional?.specialty.orEmpty(),
        reason = reason,
        date = displayDate,
        time = displayTime,
        scheduledAt = scheduledAt,
        durationMinutes = durationMinutes,
        status = AppointmentStatus.fromApiValue(status)
    )
}

internal fun NurseAppointment.toAppointmentDetailUiModel(patientName: String? = null): AppointmentDetailUiModel {
    val name = professional?.fullName.orEmpty().ifBlank { "Profesional de enfermería" }
    val (displayDate, displayTime) = SpanishDateTime.formatApiDateTime(scheduledAt)
    return AppointmentDetailUiModel(
        id = id,
        professionalName = name,
        professionalInitials = initials(name),
        specialty = listOfNotNull(
            professional?.type?.takeIf(String::isNotBlank)?.let { EnumDisplayEs.professionalType(it) },
            professional?.specialty
        ).filter(String::isNotBlank).joinToString(" · "),
        reason = reason,
        date = displayDate,
        time = displayTime,
        durationMinutes = durationMinutes,
        status = AppointmentStatus.fromApiValue(status),
        contextName = patientName
    )
}

internal fun initials(name: String): String = name.trim().split(Regex("\\s+"))
    .filter(String::isNotBlank).take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")