package com.vitaltrace.app.feature.patient.data.dto.measurements

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeasurementDto(
    val id: Long,
    @SerialName("patient_id") val patientId: Long,
    @SerialName("measurement_type_id") val measurementTypeId: Long,
    val value: String,
    val unit: String,
    @SerialName("measured_at") val measuredAt: String,
    val origin: String,
    @SerialName("author_user_id") val authorUserId: Long,
    val observation: String? = null,
    @SerialName("review_status") val reviewStatus: String = "PENDING",
    @SerialName("reviewed_at") val reviewedAt: String? = null,
    @SerialName("reviewed_by") val reviewedBy: Long? = null,
    @SerialName("review_observation") val reviewObservation: String? = null,
    val reviewer: MeasurementReviewerDto? = null,
    @SerialName("measurement_type") val measurementType: MeasurementTypeDto? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("created_by") val createdBy: Long? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("updated_by") val updatedBy: Long? = null,
    @SerialName("deleted_at") val deletedAt: String? = null,
    @SerialName("deleted_by") val deletedBy: Long? = null
)

@Serializable
data class MeasurementReviewerDto(
    val id: Long,
    @SerialName("full_name") val fullName: String? = null
)

@Serializable
data class MeasurementTypeDto(
    val id: Long,
    val name: String,
    @SerialName("base_unit") val baseUnit: String,
    val decimals: Int,
    val active: Boolean,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("created_by") val createdBy: Long? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("updated_by") val updatedBy: Long? = null
)

@Serializable
data class CreateMeasurementRequestDto(
    @SerialName("measurement_type_id") val measurementTypeId: Long,
    val value: Double,
    val unit: String,
    @SerialName("measured_at") val measuredAt: String,
    val observation: String? = null
)
