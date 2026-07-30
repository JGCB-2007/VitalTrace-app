package com.vitaltrace.app.feature.patient.data.remote

import com.vitaltrace.app.core.network.ApiResponse
import com.vitaltrace.app.feature.patient.data.dto.appointments.AppointmentDto
import com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.CreateMeasurementRequestDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.MeasurementDto
import com.vitaltrace.app.feature.patient.data.dto.relatives.PatientRelativeDto
import com.vitaltrace.app.feature.patient.data.dto.summary.PatientSummaryDataDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.TreatmentDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PatientPortalApiService {
    @GET("patient/summary")
    suspend fun getSummary(): ApiResponse<PatientSummaryDataDto>

    @GET("patient/appointments")
    suspend fun getAppointments(
        @Query("status") status: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
        @Query("upcoming") upcoming: Boolean? = null,
        @Query("page") page: Int? = null
    ): PaginatedResponseDto<AppointmentDto>

    @GET("patient/measurements")
    suspend fun getMeasurements(
        @Query("measurement_type_id") measurementTypeId: Long? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
        @Query("page") page: Int? = null
    ): PaginatedResponseDto<MeasurementDto>

    @POST("patient/measurements")
    suspend fun createMeasurement(
        @Body request: CreateMeasurementRequestDto
    ): ApiResponse<MeasurementDto>

    @GET("patient/treatments")
    suspend fun getTreatments(
        @Query("status") status: String? = null,
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null,
        @Query("active") active: Boolean? = null,
        @Query("page") page: Int? = null
    ): PaginatedResponseDto<TreatmentDto>

    @GET("patient/relatives")
    suspend fun getRelatives(
        @Query("page") page: Int? = null
    ): PaginatedResponseDto<PatientRelativeDto>

    @PUT("patient/relatives/{patientRelative}/authorize")
    suspend fun authorizeRelative(
        @Path("patientRelative") patientRelativeId: Long
    ): ApiResponse<PatientRelativeDto>

    @DELETE("patient/relatives/{patientRelative}/authorize")
    suspend fun revokeRelative(
        @Path("patientRelative") patientRelativeId: Long
    ): ApiResponse<PatientRelativeDto>
}
