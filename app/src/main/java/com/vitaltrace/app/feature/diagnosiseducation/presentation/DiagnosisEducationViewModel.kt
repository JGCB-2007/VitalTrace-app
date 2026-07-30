package com.vitaltrace.app.feature.diagnosiseducation.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.medlineplus.domain.usecase.GetDiagnosisEducationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DiagnosisEducationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDiagnosisEducation: GetDiagnosisEducationUseCase
) : ViewModel() {
    private val cieCode: String = checkNotNull(savedStateHandle["cieCode"])
    private val diagnosisName: String = checkNotNull(savedStateHandle["diagnosisName"])
    private val _uiState = MutableStateFlow<DiagnosisEducationUiState>(DiagnosisEducationUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var request: Job? = null

    init { load() }
    fun retry() = load()

    private fun load() {
        if (request?.isActive == true) return
        _uiState.value = DiagnosisEducationUiState.Loading
        request = viewModelScope.launch {
            getDiagnosisEducation(cieCode, diagnosisName)
                .onSuccess { _uiState.value = if (it.items.isEmpty()) DiagnosisEducationUiState.Empty(it) else DiagnosisEducationUiState.Success(it) }
                .onFailure { _uiState.value = DiagnosisEducationUiState.Error }
        }
    }
}
