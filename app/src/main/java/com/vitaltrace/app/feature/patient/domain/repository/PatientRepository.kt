package com.vitaltrace.app.feature.patient.domain.repository

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.model.PatientProfile
import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.feature.patient.domain.model.MarkAllNotificationsReadResult

interface PatientRepository {
    suspend fun getSummary(): Result<PatientSummary>

    suspend fun getProfile(): Result<PatientProfile>

    suspend fun getClinicalHistory(): Result<ClinicalHistory>

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

    suspend fun getRelatives(page: Int? = null): Result<Page<PatientRelative>>

    suspend fun authorizeRelative(id: Long): Result<PatientRelative>

    suspend fun revokeRelative(id: Long): Result<PatientRelative>
    suspend fun getNotifications(
        read: String = "all",
        type: String? = null,
        page: Int = 1
    ): Result<Page<PatientNotification>>

    suspend fun getUnreadNotificationsCount(): Result<Int>

    suspend fun markNotificationAsRead(id: Long): Result<PatientNotification>

    suspend fun markAllNotificationsAsRead(): Result<MarkAllNotificationsReadResult>
}

