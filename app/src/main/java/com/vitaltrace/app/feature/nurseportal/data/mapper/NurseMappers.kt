package com.vitaltrace.app.feature.nurseportal.data.mapper

import com.vitaltrace.app.feature.nurseportal.data.dto.*
import com.vitaltrace.app.feature.nurseportal.domain.model.*
import com.vitaltrace.app.feature.patient.domain.model.*

fun NurseInfoDto.toDomain() = NurseInfo(id, fullName, professionalCode, specialty)
fun NurseSummaryDto.toDomain() = NurseSummary(nurse.toDomain(), assignedPatientsCount, NurseAlertsSummary(alerts.totalPending, alerts.new, alerts.critical), appointments.next.map { it.toDomain() })
fun NurseAppointmentDto.toDomain() = NurseAppointment(id, patientId, scheduledAt, durationMinutes, reason, status, professional?.let { NurseProfessional(it.id, it.fullName, it.professionalType, it.specialty?.name) })
fun NurseMeasurementDto.toDomain() = NurseMeasurement(id, patientId, measurementType?.id, measurementType?.name, measurementType?.baseUnit, value, unit, measuredAt, origin, observation, reviewStatus)
fun NursePatientDto.toDomain() = NursePatient(patientId, recordNumber, fullName, birthDate, age, sex, administrativeStatus, activeAlertsCount, criticalAlertsCount, lastMeasurement?.toDomain(), nextAppointment?.toDomain())
fun NursePatientProfileDto.toDomain() = NurseProfile(id, fullName, recordNumber, sex, birthDate, age, phone, emergencyContact?.name, emergencyContact?.phone, status)
fun NurseDiagnosisDto.toDomain() = NurseDiagnosis(id, cieCode, description, diagnosisDate, status)
fun NurseTreatmentDto.toDomain() = NurseTreatment(id, patientId, indication, startDate, endDate, status, diagnosis?.toDomain(), medications.map { NurseMedication(it.id, it.name, it.dose, it.route, it.frequency, it.schedules) })
fun NurseClinicalEvolutionDto.toDomain() = NurseEvolution(id, clinicalSummary, status, recordedAt)
fun NurseAlertDto.toDomain() = NurseAlert(id, patientId, measurementId, type, severity, status, description, generatedAt, closedAt, history.map { NurseAlertHistory(it.id, it.action, it.previousStatus, it.newStatus, it.comment, it.createdAt) })
fun NursePatientSummaryDto.toDomain() = NursePatientSummary(patient.toDomain(), recentMeasurements.map { it.toDomain() }, diagnoses.map { it.toDomain() }, activeTreatments.map { it.toDomain() }, upcomingAppointment?.toDomain(), activeAlerts.map { it.toDomain() })
fun NurseClinicalHistoryDto.toDomain() = NurseHistory(patient.toDomain(), diagnoses.map { it.toDomain() }, evolutions.map { it.toDomain() }, treatments.map { it.toDomain() }, measurements.map { it.toDomain() })
fun NurseMeasurementTypeDto.toDomain() = NurseMeasurementType(id, name, unit, decimals, active)
fun <D, M> com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto<D>.toNursePage(mapper: (D) -> M) = Page(data.map(mapper), PaginationLinks(links.first, links.last, links.prev, links.next), PaginationMeta(meta.currentPage, meta.from, meta.lastPage, meta.links.map { PaginationMetaLink(it.url, it.label, it.active) }, meta.path, meta.perPage, meta.to, meta.total))