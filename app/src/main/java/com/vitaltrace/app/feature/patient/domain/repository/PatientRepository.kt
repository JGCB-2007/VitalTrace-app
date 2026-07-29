package com.vitaltrace.app.feature.patient.domain.repository

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.Treatment

interface PatientRepository {
    suspend fun getSummary(): Result<PatientSummary>

    suspend fun getAppointments(
        status: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
        upcoming: Boolean? = null,
        page: Int? = null
    ): Result<Page<Appointment>>

    suspend fun getMeasurements(
        measurementTypeId: Long? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
        page: Int? = null
    ): Result<Page<Measurement>>

    suspend fun createMeasurement(
        measurementTypeId: Long,
        value: Double,
        unit: String,
        measuredAt: String,
        observation: String? = null
    ): Result<Measurement>

    suspend fun getTreatments(
        status: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null,
        active: Boolean? = null,
        page: Int? = null
    ): Result<Page<Treatment>>
}
