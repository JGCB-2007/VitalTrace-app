package com.vitaltrace.app.feature.relativeportal.domain.repository

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.relativeportal.domain.model.LinkedPatient

interface RelativeRepository {
    suspend fun getLinkedPatients(): Result<List<LinkedPatient>>
    suspend fun getSummary(patientId: Long): Result<PatientSummary>
    suspend fun getAppointments(patientId: Long, page: Int? = null): Result<Page<Appointment>>
    suspend fun getMeasurements(patientId: Long, page: Int? = null): Result<Page<Measurement>>
    suspend fun getTreatments(patientId: Long, page: Int? = null): Result<Page<Treatment>>
    suspend fun getClinicalHistory(patientId: Long): Result<ClinicalHistory>
}