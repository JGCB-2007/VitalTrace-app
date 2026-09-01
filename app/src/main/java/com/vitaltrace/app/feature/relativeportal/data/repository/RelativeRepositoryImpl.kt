package com.vitaltrace.app.feature.relativeportal.data.repository

import com.vitaltrace.app.feature.patient.data.mapper.toDomain
import com.vitaltrace.app.feature.patient.domain.model.*
import com.vitaltrace.app.feature.relativeportal.data.remote.RelativePortalApiService
import com.vitaltrace.app.feature.relativeportal.domain.model.LinkedPatient
import com.vitaltrace.app.feature.relativeportal.domain.repository.RelativeRepository
import com.vitaltrace.app.feature.relativeportal.domain.selection.RelativePatientSelection
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RelativePortalException(val httpCode: Int?, message: String, cause: Throwable? = null) : Exception(message, cause)

class RelativeRepositoryImpl @Inject constructor(
    private val api: RelativePortalApiService,
    private val selection: RelativePatientSelection
) : RelativeRepository {
    override suspend fun getLinkedPatients() = execute {
        api.getLinkedPatients().data.orEmpty().map {
            LinkedPatient(it.id, it.fullName.orEmpty(), it.relationship, it.status, it.avatarUrl)
        }
    }

    override suspend fun getSummary(patientId: Long) = execute {
        api.getSummary(patientId).data?.toDomain() ?: error("No se recibió el resumen del paciente.")
    }

    override suspend fun getAppointments(patientId: Long, page: Int?) = execute {
        api.getAppointments(patientId, page).toDomain { it.toDomain() }
    }

    override suspend fun getMeasurements(patientId: Long, page: Int?) = execute {
        api.getMeasurements(patientId, page).toDomain { it.toDomain() }
    }

    override suspend fun getTreatments(patientId: Long, page: Int?) = execute {
        api.getTreatments(patientId, page).toDomain { it.toDomain() }
    }

    override suspend fun getClinicalHistory(patientId: Long) = execute {
        api.getClinicalHistory(patientId).data?.toDomain() ?: error("No se recibió el historial clínico.")
    }

    private suspend fun <T> execute(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (exception: HttpException) {
        if (exception.code() == 403) selection.clear()
        Result.failure(RelativePortalException(exception.code(), messageFor(exception.code()), exception))
    } catch (exception: IOException) {
        Result.failure(RelativePortalException(null, "No se pudo conectar con el servidor.", exception))
    } catch (exception: Exception) {
        Result.failure(RelativePortalException(null, exception.message ?: "Ocurrió un error inesperado.", exception))
    }

    private fun messageFor(code: Int): String = when (code) {
        401 -> "Tu sesión ha expirado."
        403 -> "No tienes autorización para ver la información de este paciente."
        404 -> "El recurso solicitado no está disponible."
        else -> "No pudimos cargar la información del paciente."
    }
}
