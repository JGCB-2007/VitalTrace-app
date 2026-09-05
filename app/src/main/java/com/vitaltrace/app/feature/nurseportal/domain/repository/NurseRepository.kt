package com.vitaltrace.app.feature.nurseportal.domain.repository

import com.vitaltrace.app.feature.nurseportal.domain.model.*
import com.vitaltrace.app.feature.patient.domain.model.Page

interface NurseRepository {
    suspend fun getDashboard(): Result<NurseSummary>
    suspend fun getAssignedPatients(search: String? = null, page: Int? = null): Result<Page<NursePatient>>
    suspend fun getPatientProfile(patientId: Long): Result<NurseProfile>
    suspend fun getPatientSummary(patientId: Long): Result<NursePatientSummary>
    suspend fun getAppointments(page: Int? = null): Result<Page<NurseAppointment>>
    suspend fun getPatientAppointments(patientId: Long, page: Int? = null): Result<Page<NurseAppointment>>
    suspend fun getAppointmentDetail(id: Long): Result<NurseAppointment>
    suspend fun getMeasurements(patientId: Long, typeId: Long? = null, page: Int? = null): Result<Page<NurseMeasurement>>
    suspend fun createMeasurement(patientId: Long, typeId: Long, value: Double, unit: String, measuredAt: String, observation: String?): Result<NurseMeasurement>
    suspend fun getMeasurementTypes(): Result<List<NurseMeasurementType>>
    suspend fun getDiagnoses(patientId: Long, page: Int? = null): Result<Page<NurseDiagnosis>>
    suspend fun getTreatments(patientId: Long, page: Int? = null): Result<Page<NurseTreatment>>
    suspend fun getClinicalHistory(patientId: Long): Result<NurseHistory>
    suspend fun getAlerts(status: String? = null, severity: String? = null, patientId: Long? = null, page: Int? = null): Result<Page<NurseAlert>>
    suspend fun getPatientAlerts(patientId: Long, page: Int? = null): Result<Page<NurseAlert>>
    suspend fun getAlertDetail(id: Long): Result<NurseAlert>
    suspend fun classifyAlert(id: Long, comment: String?): Result<NurseAlert>
    suspend fun escalateAlert(id: Long, comment: String?): Result<NurseAlert>
}