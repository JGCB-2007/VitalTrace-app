package com.vitaltrace.app.feature.patient.data.repository

import com.vitaltrace.app.feature.patient.data.dto.measurements.CreateMeasurementRequestDto
import com.vitaltrace.app.feature.patient.data.mapper.toDomain
import com.vitaltrace.app.feature.patient.data.remote.PatientPortalApiService
import com.vitaltrace.app.feature.patient.domain.exception.PatientException
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.PatientRelative
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val apiService: PatientPortalApiService
) : PatientRepository {
    override suspend fun getSummary(): Result<PatientSummary> = execute {
        apiService.getSummary().data?.toDomain()
            ?: throw PatientException("Patient summary data was not received.")
    }

    override suspend fun getAppointments(
        status: String?, dateFrom: String?, dateTo: String?, upcoming: Boolean?, page: Int?
    ): Result<Page<Appointment>> = execute {
        apiService.getAppointments(status, dateFrom, dateTo, upcoming, page).toDomain { it.toDomain() }
    }

    override suspend fun getMeasurements(
        measurementTypeId: Long?, dateFrom: String?, dateTo: String?, page: Int?
    ): Result<Page<Measurement>> = execute {
        apiService.getMeasurements(measurementTypeId, dateFrom, dateTo, page).toDomain { it.toDomain() }
    }

    override suspend fun createMeasurement(
        measurementTypeId: Long, value: Double, unit: String, measuredAt: String, observation: String?
    ): Result<Measurement> = execute {
        val request = CreateMeasurementRequestDto(measurementTypeId, value, unit, measuredAt, observation)
        apiService.createMeasurement(request).data?.toDomain()
            ?: throw PatientException("The registered measurement was not received.")
    }

    override suspend fun getTreatments(
        status: String?, dateFrom: String?, dateTo: String?, active: Boolean?, page: Int?
    ): Result<Page<Treatment>> = execute {
        apiService.getTreatments(status, dateFrom, dateTo, active, page).toDomain { it.toDomain() }
    }

    override suspend fun getRelatives(page: Int?): Result<Page<PatientRelative>> = execute {
        apiService.getRelatives(page).toDomain { it.toDomain() }
    }

    override suspend fun authorizeRelative(id: Long): Result<PatientRelative> = execute {
        apiService.authorizeRelative(id).data?.toDomain()
            ?: throw PatientException("The authorized relative was not received.")
    }

    override suspend fun revokeRelative(id: Long): Result<PatientRelative> = execute {
        apiService.revokeRelative(id).data?.toDomain()
            ?: throw PatientException("The revoked relative was not received.")
    }

    private suspend fun <T> execute(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (exception: PatientException) {
        Result.failure(exception)
    } catch (exception: HttpException) {
        Result.failure(PatientException(getHttpErrorMessage(exception.code()), exception))
    } catch (exception: IOException) {
        Result.failure(PatientException(exception.message ?: "Could not connect to the server.", exception))
    } catch (exception: Exception) {
        Result.failure(PatientException(exception.message ?: "An unexpected patient portal error occurred.", exception))
    }

    private fun getHttpErrorMessage(code: Int): String = when (code) {
        400 -> "The request could not be processed."
        401 -> "Your session has expired."
        403 -> "You do not have permission to perform this action."
        404 -> "The requested patient resource was not found."
        408 -> "The server took too long to respond."
        409 -> "The request conflicts with the current server state."
        422 -> "The information provided is invalid."
        429 -> "Too many requests. Please try again later."
        500 -> "The server encountered an internal error."
        502, 503 -> "The server is temporarily unavailable."
        else -> "The patient portal request failed."
    }
}
