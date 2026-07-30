package com.vitaltrace.app.feature.patient.data.mapper

import com.vitaltrace.app.feature.patient.data.dto.appointments.AppointmentDto
import com.vitaltrace.app.feature.patient.data.dto.appointments.ProfessionalDto
import com.vitaltrace.app.feature.patient.data.dto.appointments.SpecialtyDto
import com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.MeasurementDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.MeasurementTypeDto
import com.vitaltrace.app.feature.patient.data.dto.summary.PatientSummaryDataDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.DiagnosisDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.PrescriberDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.TreatmentDto
import com.vitaltrace.app.feature.patient.data.dto.relatives.PatientRelativeDto
import com.vitaltrace.app.feature.patient.data.dto.relatives.RelativeDto
import com.vitaltrace.app.feature.patient.data.dto.relatives.RelativePersonDto
import com.vitaltrace.app.feature.patient.domain.model.AlertsSummary
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Diagnosis
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.MeasurementReviewer
import com.vitaltrace.app.feature.patient.domain.model.MeasurementType
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PaginationLinks
import com.vitaltrace.app.feature.patient.domain.model.PaginationMeta
import com.vitaltrace.app.feature.patient.domain.model.PaginationMetaLink
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.Prescriber
import com.vitaltrace.app.feature.patient.domain.model.Professional
import com.vitaltrace.app.feature.patient.domain.model.Specialty
import com.vitaltrace.app.feature.patient.domain.model.SummaryPatient
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.model.Relative
import com.vitaltrace.app.feature.patient.domain.model.RelativePerson

fun PatientSummaryDataDto.toDomain() = PatientSummary(
    patient = SummaryPatient(
        id = patient.id,
        recordNumber = patient.recordNumber,
        administrativeStatus = patient.administrativeStatus,
        fullName = patient.fullName
    ),
    nextAppointment = nextAppointment?.toDomain(),
    latestMeasurements = latestMeasurements.map(MeasurementDto::toDomain),
    activeTreatments = activeTreatments.map(TreatmentDto::toDomain),
    alerts = AlertsSummary(open = alertsSummary.open, critical = alertsSummary.critical)
)

fun AppointmentDto.toDomain() = Appointment(
    id = id,
    scheduledAt = scheduledAt,
    durationMinutes = durationMinutes,
    reason = reason,
    status = status,
    professional = professional?.toDomain()
)

fun ProfessionalDto.toDomain() = Professional(
    id = id,
    professionalType = professionalType,
    fullName = fullName,
    specialty = specialty?.toDomain()
)

fun SpecialtyDto.toDomain() = Specialty(id = id, name = name)

fun MeasurementDto.toDomain() = Measurement(
    id = id,
    patientId = patientId,
    measurementTypeId = measurementTypeId,
    value = value,
    unit = unit,
    measuredAt = measuredAt,
    origin = origin,
    authorUserId = authorUserId,
    observation = observation,
    reviewStatus = reviewStatus,
    reviewedAt = reviewedAt,
    reviewedBy = reviewedBy,
    reviewObservation = reviewObservation,
    reviewer = reviewer?.let { MeasurementReviewer(it.id, it.fullName) },
    measurementType = measurementType?.toDomain()
)

fun MeasurementTypeDto.toDomain() = MeasurementType(
    id = id,
    name = name,
    baseUnit = baseUnit,
    decimals = decimals,
    active = active
)

fun TreatmentDto.toDomain() = Treatment(
    id = id,
    diagnosisId = diagnosisId,
    diagnosis = diagnosis?.toDomain(),
    indications = indications,
    startDate = startDate,
    endDate = endDate,
    status = status,
    prescribedBy = prescribedBy,
    prescriber = prescriber?.toDomain()
)

fun PatientRelativeDto.toDomain() = PatientRelative(
    id = id,
    patientId = patientId,
    relativeId = relativeId,
    relationship = relationship,
    status = status,
    startDate = startDate,
    endDate = endDate,
    relative = relative?.toDomain()
)

private fun RelativeDto.toDomain() = Relative(id, personId, person?.toDomain())

private fun RelativePersonDto.toDomain() = RelativePerson(
    id, firstName, middleName, firstLastName, secondLastName, phone
)

fun DiagnosisDto.toDomain() = Diagnosis(
    id = id,
    cieCode = cieCode,
    description = description,
    diagnosisDate = diagnosisDate,
    status = status
)

fun PrescriberDto.toDomain() = Prescriber(
    id = id,
    professionalType = professionalType,
    fullName = fullName,
    specialty = specialty?.toDomain()
)

fun <D, M> PaginatedResponseDto<D>.toDomain(mapItem: (D) -> M): Page<M> = Page(
    items = data.map(mapItem),
    links = PaginationLinks(
        first = links.first,
        last = links.last,
        previous = links.prev,
        next = links.next
    ),
    meta = PaginationMeta(
        currentPage = meta.currentPage,
        from = meta.from,
        lastPage = meta.lastPage,
        links = meta.links.map { PaginationMetaLink(it.url, it.label, it.active) },
        path = meta.path,
        perPage = meta.perPage,
        to = meta.to,
        total = meta.total
    )
)
