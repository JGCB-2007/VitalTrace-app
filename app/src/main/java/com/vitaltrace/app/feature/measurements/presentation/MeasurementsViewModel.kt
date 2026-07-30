package com.vitaltrace.app.feature.measurements.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientMeasurementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeasurementsViewModel @Inject constructor(
    private val getPatientMeasurements: GetPatientMeasurementsUseCase,
    private val measurementsMapper: MeasurementsMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(MeasurementsUiState())
    val uiState = _uiState.asStateFlow()
    private var measurementsRequest: Job? = null

    init {
        loadMeasurements()
    }

    fun retry() = loadMeasurements()

    fun selectFilter(filter: MeasurementFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun showMeasurementDetail(measurementId: Long) {
        val content = (_uiState.value.contentState as? MeasurementsContentState.Success)?.content
            ?: return
        val measurement = sequenceOf(content.latestMeasurement)
            .plus(content.measurements.asSequence())
            .filterNotNull()
            .firstOrNull { it.id == measurementId }
            ?: return
        _uiState.update { it.copy(selectedMeasurementDetail = measurement.toDetail()) }
    }

    fun dismissMeasurementDetail() {
        _uiState.update { it.copy(selectedMeasurementDetail = null) }
    }

    private fun loadMeasurements() {
        if (measurementsRequest?.isActive == true) return
        _uiState.update {
            it.copy(
                contentState = MeasurementsContentState.Loading,
                selectedMeasurementDetail = null
            )
        }
        measurementsRequest = viewModelScope.launch {
            getPatientMeasurements()
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            contentState = MeasurementsContentState.Success(
                                measurementsMapper.map(page)
                            )
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            contentState = MeasurementsContentState.Error(
                                "No pudimos cargar tus mediciones. Intenta de nuevo."
                            )
                        )
                    }
                }
        }
    }
}
