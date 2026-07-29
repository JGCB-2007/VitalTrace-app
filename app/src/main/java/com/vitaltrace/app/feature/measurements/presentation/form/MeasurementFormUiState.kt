package com.vitaltrace.app.feature.measurements.presentation.form

import java.time.LocalDate
import java.time.LocalTime

data class MeasurementFormUiState(
    val systolic: String = "145",
    val diastolic: String = "92",
    val date: LocalDate = LocalDate.of(2026, 7, 14),
    val time: LocalTime = LocalTime.of(9, 42),
    val note: String = "",
    val systolicError: MeasurementFieldError? = null,
    val diastolicError: MeasurementFieldError? = null,
    val isSaving: Boolean = false
)

enum class MeasurementFieldError {
    REQUIRED,
    INVALID
}
