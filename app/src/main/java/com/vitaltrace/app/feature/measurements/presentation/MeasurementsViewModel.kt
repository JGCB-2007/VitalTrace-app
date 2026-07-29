package com.vitaltrace.app.feature.measurements.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MeasurementsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(sampleMeasurementsState())
    val uiState = _uiState.asStateFlow()

    fun selectFilter(filter: MeasurementFilter) {
        _uiState.update { state ->
            state.copy(selectedFilter = filter)
        }
    }

    fun retry() {
        _uiState.value = sampleMeasurementsState()
    }

    fun showMeasurementDetail(measurementId: String) {
        _uiState.update { state ->
            state.copy(
                selectedMeasurementDetail = sampleMeasurementDetail(measurementId)
            )
        }
    }

    fun dismissMeasurementDetail() {
        _uiState.update { state ->
            state.copy(selectedMeasurementDetail = null)
        }
    }
}

private fun sampleMeasurementDetail(measurementId: String): MeasurementDetailUiModel {
    return MeasurementDetailUiModel(
        id = measurementId,
        value = "145/92",
        date = "14 jul 2026",
        time = "9:42 a. m.",
        observation = "En reposo",
        status = MeasurementStatus.REVIEWED,
        followUp = MeasurementFollowUpUiModel(
            message = "Dr. Carlos Ruiz revisó el registro y dejó una observación de seguimiento.",
            date = "15 jul",
            time = "11:20 a. m."
        )
    )
}

private fun sampleMeasurementsState(): MeasurementsUiState {
    return MeasurementsUiState(
        latestMeasurement = MeasurementUiModel(
            id = "latest",
            value = "145/92",
            date = "14 jul",
            time = "9:42 a. m.",
            status = MeasurementStatus.IN_REVIEW
        ),
        measurements = listOf(
            MeasurementUiModel(
                id = "measurement-1",
                value = "138/88",
                date = "13 jul",
                time = "8:15 a. m.",
                status = MeasurementStatus.REVIEWED
            ),
            MeasurementUiModel(
                id = "measurement-2",
                value = "132/85",
                date = "11 jul",
                time = "8:02 a. m.",
                status = MeasurementStatus.REVIEWED
            ),
            MeasurementUiModel(
                id = "measurement-3",
                value = "129/84",
                date = "9 jul",
                time = "7:58 a. m.",
                status = MeasurementStatus.REVIEWED
            )
        )
    )
}
