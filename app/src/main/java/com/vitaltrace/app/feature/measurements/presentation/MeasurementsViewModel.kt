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

    fun refresh() {
        measurementsRequest?.cancel()
        measurementsRequest = null
        loadMeasurements(refresh = true)
    }

    fun loadMore() {
        val content = (_uiState.value.contentState as? MeasurementsContentState.Success)?.content ?: return
        if (_uiState.value.isLoadingMore || content.currentPage >= content.lastPage) return
        loadMeasurements(page = content.currentPage + 1)
    }

    fun refreshAfterMeasurementCreated() {
        measurementsRequest?.cancel()
        measurementsRequest = null
        loadMeasurements()
    }

    fun selectFilter(filter: MeasurementFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun updateQuery(value: String) = _uiState.update { it.copy(query = value.take(80)) }

    fun selectType(typeName: String?) = _uiState.update { it.copy(selectedTypeName = typeName) }

    fun selectPeriod(period: MeasurementPeriod) = _uiState.update { it.copy(selectedPeriod = period) }

    fun toggleAttentionOnly() = _uiState.update { it.copy(attentionOnly = !it.attentionOnly) }

    fun clearAdvancedFilters() = _uiState.update {
        it.copy(query = "", selectedTypeName = null, selectedPeriod = MeasurementPeriod.DAYS_30, attentionOnly = false)
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

    private fun loadMeasurements(page: Int = 1, refresh: Boolean = false) {
        if (measurementsRequest?.isActive == true) return
        _uiState.update { state ->
            when {
                refresh -> state.copy(isRefreshing = true, selectedMeasurementDetail = null)
                page > 1 -> state.copy(isLoadingMore = true)
                else -> state.copy(
                    contentState = MeasurementsContentState.Loading,
                    selectedMeasurementDetail = null
                )
            }
        }
        measurementsRequest = viewModelScope.launch {
            getPatientMeasurements(page, forceRefresh = refresh)
                .onSuccess { page ->
                    _uiState.update {
                        val mapped = measurementsMapper.map(page)
                        val previous = (it.contentState as? MeasurementsContentState.Success)?.content
                        val content = if (page.meta.currentPage > 1 && previous != null) {
                            val combined = (listOfNotNull(previous.latestMeasurement) + previous.measurements +
                                listOfNotNull(mapped.latestMeasurement) + mapped.measurements)
                                .distinctBy(MeasurementUiModel::id)
                            measurementsMapper.mapUiModels(combined).copy(
                                currentPage = page.meta.currentPage,
                                lastPage = page.meta.lastPage
                            )
                        } else mapped
                        it.copy(
                            contentState = MeasurementsContentState.Success(content),
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        if (page > 1 || refresh) it.copy(isRefreshing = false, isLoadingMore = false)
                        else it.copy(
                            contentState = MeasurementsContentState.Error("No pudimos cargar tus mediciones. Intenta de nuevo."),
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
        }
    }
}
