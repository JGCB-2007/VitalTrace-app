package com.vitaltrace.app.feature.nurseportal.data.remote

import com.vitaltrace.app.core.network.ApiResponse
import com.vitaltrace.app.feature.nurseportal.data.dto.*
import com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto
import retrofit2.http.*

interface NursePortalApiService {
    @GET("nurse/summary") suspend fun getSummary(): ApiResponse<NurseSummaryDto>
    @GET("nurse/patients") suspend fun getPatients(@Query("search") search: String? = null, @Query("page") page: Int? = null): PaginatedResponseDto<NursePatientDto>
    @GET("nurse/patients/{patientId}/profile") suspend fun getPatientProfile(@Path("patientId") patientId: Long): ApiResponse<NursePatientProfileDto>
    @GET("nurse/patients/{patientId}/summary") suspend fun getPatientSummary(@Path("patientId") patientId: Long): ApiResponse<NursePatientSummaryDto>
    @GET("nurse/appointments") suspend fun getAppointments(@Query("page") page: Int? = null): PaginatedResponseDto<NurseAppointmentDto>
    @GET("nurse/patients/{patientId}/appointments") suspend fun getPatientAppointments(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<NurseAppointmentDto>
    @GET("nurse/appointments/{appointmentId}") suspend fun getAppointmentDetail(@Path("appointmentId") id: Long): ApiResponse<NurseAppointmentDto>
    @GET("nurse/patients/{patientId}/measurements") suspend fun getPatientMeasurements(@Path("patientId") patientId: Long, @Query("page") page: Int? = null, @Query("measurement_type_id") measurementTypeId: Long? = null): PaginatedResponseDto<NurseMeasurementDto>
    @POST("nurse/patients/{patientId}/measurements") suspend fun createMeasurement(@Path("patientId") patientId: Long, @Body request: NurseMeasurementRequestDto): ApiResponse<NurseMeasurementDto>
    @GET("nurse/measurement-types") suspend fun getMeasurementTypes(): ApiResponse<List<NurseMeasurementTypeDto>>
    @GET("nurse/patients/{patientId}/diagnoses") suspend fun getPatientDiagnoses(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<NurseDiagnosisDto>
    @GET("nurse/patients/{patientId}/treatments") suspend fun getPatientTreatments(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<NurseTreatmentDto>
    @GET("nurse/patients/{patientId}/clinical-history") suspend fun getClinicalHistory(@Path("patientId") patientId: Long): ApiResponse<NurseClinicalHistoryDto>
    @GET("nurse/alerts") suspend fun getAlerts(@Query("status") status: String? = null, @Query("severity") severity: String? = null, @Query("patient_id") patientId: Long? = null, @Query("page") page: Int? = null): PaginatedResponseDto<NurseAlertDto>
    @GET("nurse/patients/{patientId}/alerts") suspend fun getPatientAlerts(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<NurseAlertDto>
    @GET("nurse/alerts/{alertId}") suspend fun getAlertDetail(@Path("alertId") id: Long): ApiResponse<NurseAlertDto>
    @POST("nurse/alerts/{alertId}/classify") suspend fun classifyAlert(@Path("alertId") id: Long, @Body request: NurseAlertActionDto): ApiResponse<NurseAlertDto>
    @POST("nurse/alerts/{alertId}/escalate") suspend fun escalateAlert(@Path("alertId") id: Long, @Body request: NurseAlertActionDto): ApiResponse<NurseAlertDto>
}