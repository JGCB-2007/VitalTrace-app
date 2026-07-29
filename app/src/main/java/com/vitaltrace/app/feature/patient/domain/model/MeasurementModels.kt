package com.vitaltrace.app.feature.patient.domain.model

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
    val measurementType: MeasurementType?
)

data class MeasurementType(
    val id: Long,
    val name: String,
    val baseUnit: String,
    val decimals: Int,
    val active: Boolean
)
