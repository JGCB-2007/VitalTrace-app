package com.vitaltrace.app.feature.clinicalhistory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetClinicalHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ClinicalHistoryViewModel @Inject constructor(private val getClinicalHistory: GetClinicalHistoryUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow<ClinicalHistoryUiState>(ClinicalHistoryUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var request: Job? = null

    init { load() }
    fun retry() = load()

    private fun load() {
        if (request?.isActive == true) return
        _uiState.value = ClinicalHistoryUiState.Loading
        request = viewModelScope.launch {
            getClinicalHistory().onSuccess { history ->
                val empty = history.diagnoses.isEmpty() && history.clinicalEvolutions.isEmpty() &&
                    history.currentTreatments.isEmpty() && history.recentMeasurements.isEmpty()
                _uiState.value = if (empty) ClinicalHistoryUiState.Empty(history.recordNumber)
                    else ClinicalHistoryUiState.Success(history)
            }.onFailure {
                _uiState.value = ClinicalHistoryUiState.Error("No pudimos cargar tu historial clínico. Intenta de nuevo.")
            }
        }
    }
}
