package com.vitaltrace.app.feature.measurements.presentation.form

sealed interface MeasurementFormUiEffect {
    data object MeasurementSaved : MeasurementFormUiEffect
}
