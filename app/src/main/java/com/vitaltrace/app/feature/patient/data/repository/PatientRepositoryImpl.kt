package com.vitaltrace.app.feature.patient.data.repository

import com.vitaltrace.app.core.cache.PatientMemoryCache
import com.vitaltrace.app.core.presentation.localization.HttpMessagesEs
import com.vitaltrace.app.feature.patient.data.dto.measurements.CreateMeasurementRequestDto
import com.vitaltrace.app.feature.patient.data.mapper.toDomain
import com.vitaltrace.app.feature.patient.data.remote.PatientPortalApiService
import com.vitaltrace.app.feature.patient.domain.exception.PatientException
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.MarkAllNotificationsReadResult
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.model.PatientProfile
import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val apiService: PatientPortalApiService,
    private val cache: PatientMemoryCache
) : PatientRepository {
    override suspend fun getSummary(): Result<PatientSummary> = execute {
        apiService.getSummary().data?.toDomain()
            ?: throw PatientException("No recibimos el resumen del paciente. Intenta de nuevo.")
    }

    override suspend fun getProfile(): Result<PatientProfile> = execute {
        cache.get<PatientProfile>("profile")?.let { return@execute it }
        apiService.getProfile().data?.toDomain()?.also { cache.put("profile", it) }
            ?: throw PatientException("No recibimos el perfil del paciente. Intenta de nuevo.")
    }

    override suspend fun getClinicalHistory(): Result<ClinicalHistory> = execute {
        cache.get<ClinicalHistory>("clinical-history")?.let { return@execute it }
        apiService.getClinicalHistory().data?.toDomain()?.also { cache.put("clinical-history", it) }
            ?: throw PatientException("No recibimos el historial clínico del paciente. Intenta de nuevo.")
    }

    override suspend fun getAppointments(
        status: String?, dateFrom: String?, dateTo: String?, upcoming: Boolean?, page: Int?
    ): Result<Page<Appointment>> = execute {
        apiService.getAppointments(status, dateFrom, dateTo, upcoming, page)
            .toDomain { it.toDomain() }
    }

    override suspend fun getMeasurements(
        measurementTypeId: Long?, dateFrom: String?, dateTo: String?, page: Int?
    ): Result<Page<Measurement>> = execute {
        val key = "measurements:$measurementTypeId:$dateFrom:$dateTo:$page"
        cache.get<Page<Measurement>>(key)?.let { return@execute it }
        apiService.getMeasurements(measurementTypeId, dateFrom, dateTo, page)
            .toDomain { it.toDomain() }.also { cache.put(key, it) }
    }

    override suspend fun createMeasurement(
        measurementTypeId: Long, value: Double, unit: String, measuredAt: String, observation: String?
    ): Result<Measurement> = execute {
        val request = CreateMeasurementRequestDto(measurementTypeId, value, unit, measuredAt, observation)
        apiService.createMeasurement(request).data?.toDomain()
            ?.also {
                cache.invalidate("measurements:")
                cache.invalidate("summary")
                cache.invalidate("clinical-history")
            }
            ?: throw PatientException("No recibimos la medición registrada. Intenta de nuevo.")
    }

    override suspend fun getTreatments(
        status: String?, dateFrom: String?, dateTo: String?, active: Boolean?, page: Int?
    ): Result<Page<Treatment>> = execute {
        val key = "treatments:$status:$dateFrom:$dateTo:$active:$page"
        cache.get<Page<Treatment>>(key)?.let { return@execute it }
        apiService.getTreatments(status, dateFrom, dateTo, active, page)
            .toDomain { it.toDomain() }.also { cache.put(key, it) }
    }

    override suspend fun getRelatives(page: Int?): Result<Page<PatientRelative>> = execute {
        apiService.getRelatives(page).toDomain { it.toDomain() }
    }

    override suspend fun authorizeRelative(id: Long): Result<PatientRelative> = execute {
        apiService.authorizeRelative(id).data?.toDomain()
            ?: throw PatientException("No recibimos los datos del familiar autorizado. Intenta de nuevo.")
    }

    override suspend fun revokeRelative(id: Long): Result<PatientRelative> = execute {
        apiService.revokeRelative(id).data?.toDomain()
            ?: throw PatientException("No recibimos los datos del familiar revocado. Intenta de nuevo.")
    }

    override suspend fun getNotifications(
        read: String, type: String?, page: Int
    ): Result<Page<PatientNotification>> = execute {
        val key = "notifications:$read:$type:$page"
        cache.get<Page<PatientNotification>>(key)?.let { return@execute it }
        apiService.getNotifications(read, type, page)
            .toDomain { it.toDomain() }.also { cache.put(key, it) }
    }

    override suspend fun getUnreadNotificationsCount(): Result<Int> = execute {
        apiService.getUnreadNotificationsCount().data?.unreadCount
            ?: throw PatientException("No recibimos el número de notificaciones sin leer. Intenta de nuevo.")
    }

    override suspend fun markNotificationAsRead(id: Long): Result<PatientNotification> = execute {
        apiService.markNotificationAsRead(id).data?.toDomain()
            ?.also { cache.invalidate("notifications") }
            ?: throw PatientException("No recibimos la notificación actualizada. Intenta de nuevo.")
    }

    override suspend fun markAllNotificationsAsRead(): Result<MarkAllNotificationsReadResult> = execute {
        apiService.markAllNotificationsAsRead().data?.let {
            MarkAllNotificationsReadResult(it.updatedCount, it.unreadCount)
        } ?: throw PatientException("No recibimos el resultado de la actualización de notificaciones. Intenta de nuevo.")
    }

    private suspend fun <T> execute(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (exception: PatientException) {
        Result.failure(exception)
    } catch (exception: HttpException) {
        Result.failure(PatientException(getHttpErrorMessage(exception.code()), exception))
    } catch (exception: IOException) {
        Result.failure(PatientException(HttpMessagesEs.NETWORK, exception))
    } catch (exception: Exception) {
        Result.failure(PatientException(HttpMessagesEs.UNEXPECTED, exception))
    }

    private fun getHttpErrorMessage(code: Int): String = HttpMessagesEs.forStatus(code)
}

