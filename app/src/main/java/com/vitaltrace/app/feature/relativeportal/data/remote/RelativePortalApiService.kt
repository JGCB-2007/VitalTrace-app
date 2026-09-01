package com.vitaltrace.app.feature.relativeportal.data.remote

import com.vitaltrace.app.core.network.ApiResponse
import com.vitaltrace.app.feature.patient.data.dto.appointments.AppointmentDto
import com.vitaltrace.app.feature.patient.data.dto.clinicalhistory.ClinicalHistoryDto
import com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.MeasurementDto
import com.vitaltrace.app.feature.patient.data.dto.summary.PatientSummaryDataDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.TreatmentDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class LinkedPatientDto(
    val id: Long,
    @SerialName("full_name") val fullName: String? = null,
    val relationship: String,
    val status: String,
    @SerialName("avatar_url") val avatarUrl: String? = null
)

interface RelativePortalApiService {
    @GET("relative/patients")
    suspend fun getLinkedPatients(): ApiResponse<List<LinkedPatientDto>>

    @GET("relative/patients/{patientId}/summary")
    suspend fun getSummary(@Path("patientId") patientId: Long): ApiResponse<PatientSummaryDataDto>

    @GET("relative/patients/{patientId}/appointments")
    suspend fun getAppointments(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<AppointmentDto>

    @GET("relative/patients/{patientId}/measurements")
    suspend fun getMeasurements(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<MeasurementDto>

    @GET("relative/patients/{patientId}/treatments")
    suspend fun getTreatments(@Path("patientId") patientId: Long, @Query("page") page: Int? = null): PaginatedResponseDto<TreatmentDto>

    @GET("relative/patients/{patientId}/clinical-history")
    suspend fun getClinicalHistory(@Path("patientId") patientId: Long): ApiResponse<ClinicalHistoryDto>
}