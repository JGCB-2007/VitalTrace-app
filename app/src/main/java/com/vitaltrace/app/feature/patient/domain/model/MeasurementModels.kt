package com.vitaltrace.app.feature.patient.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Measurement(
    val id: Long,
    val patientId: Long,
    val measurementTypeId: Long,
    val value: String,
    val unit: String,
    val measuredAt: String,
    val origin: String,
    val authorUserId: Long,
    val observation: String?,
    val reviewStatus: String,
    val reviewedAt: String?,
    val reviewedBy: Long?,
    val reviewObservation: String?,
    val reviewer: MeasurementReviewer?,
    val measurementType: MeasurementType?
)

@Serializable data class MeasurementReviewer(
    val id: Long,
    val fullName: String?
)

@Serializable data class MeasurementType(
    val id: Long,
    val name: String,
    val baseUnit: String,
    val decimals: Int,
    val active: Boolean
)
