package com.vitaltrace.app.feature.treatments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientTreatmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TreatmentsViewModel @Inject constructor(
    private val getPatientTreatments: GetPatientTreatmentsUseCase,
    private val treatmentsMapper: TreatmentsMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(TreatmentsUiState())
    val uiState = _uiState.asStateFlow()
    private var treatmentsRequest: Job? = null

    init {
        loadTreatments()
    }

    fun retry() = loadTreatments()

    fun selectTreatment(id: Long) {
        val treatments = (_uiState.value.contentState as? TreatmentsContentState.Success)
            ?.content?.treatments.orEmpty()
        _uiState.update { it.copy(selectedTreatment = treatments.firstOrNull { item -> item.id == id }) }
    }

    private fun loadTreatments() {
        if (treatmentsRequest?.isActive == true) return
        _uiState.update { it.copy(contentState = TreatmentsContentState.Loading) }
        treatmentsRequest = viewModelScope.launch {
            getPatientTreatments()
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(contentState = TreatmentsContentState.Success(treatmentsMapper.map(page)))
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            contentState = TreatmentsContentState.Error(
                                "No pudimos cargar tus medicamentos. Intenta de nuevo."
                            )
                        )
                    }
                }
        }
    }
}
