package com.vitaltrace.app.feature.measurements.presentation.form

import java.time.LocalDate
import java.time.LocalTime

data class MeasurementFormUiState(
    val availableTypes: List<MeasurementTypeOption> = emptyList(),
    val selectedTypeId: Long? = null,
    val value: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now().withSecond(0).withNano(0),
    val note: String = "",
    val typeError: MeasurementFieldError? = null,
    val valueError: MeasurementFieldError? = null,
    val errorMessage: String? = null,
    val isLoadingTypes: Boolean = true,
    val isSaving: Boolean = false
) {
    val selectedType: MeasurementTypeOption?
        get() = availableTypes.firstOrNull { it.id == selectedTypeId }
}

data class MeasurementTypeOption(
    val id: Long,
    val name: String,
    val unit: String,
    val decimals: Int
)

enum class MeasurementFieldError {
    REQUIRED,
    INVALID
}
