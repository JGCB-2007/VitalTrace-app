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

    fun refresh() {
        treatmentsRequest?.cancel()
        treatmentsRequest = null
        loadTreatments(refresh = true)
    }

    fun loadMore() {
        val content = (_uiState.value.contentState as? TreatmentsContentState.Success)?.content ?: return
        if (_uiState.value.isLoadingMore || content.currentPage >= content.lastPage) return
        loadTreatments(page = content.currentPage + 1)
    }

    fun selectTreatment(id: Long) {
        val treatments = (_uiState.value.contentState as? TreatmentsContentState.Success)
            ?.content?.treatments.orEmpty()
        _uiState.update { it.copy(selectedTreatment = treatments.firstOrNull { item -> item.id == id }) }
    }

    fun updateQuery(value: String) = _uiState.update { it.copy(query = value.take(80)) }
    fun toggleActiveOnly() = _uiState.update { it.copy(activeOnly = !it.activeOnly) }

    private fun loadTreatments(page: Int = 1, refresh: Boolean = false) {
        if (treatmentsRequest?.isActive == true) return
        _uiState.update { state ->
            when {
                refresh -> state.copy(isRefreshing = true)
                page > 1 -> state.copy(isLoadingMore = true)
                else -> state.copy(contentState = TreatmentsContentState.Loading)
            }
        }
        treatmentsRequest = viewModelScope.launch {
            getPatientTreatments(page = page, forceRefresh = refresh)
                .onSuccess { page ->
                    _uiState.update {
                        val mapped = treatmentsMapper.map(page)
                        val previous = (it.contentState as? TreatmentsContentState.Success)?.content
                        val content = if (page.meta.currentPage > 1 && previous != null) {
                            mapped.copy(
                                treatments = (previous.treatments + mapped.treatments).distinctBy(TreatmentUiModel::id)
                            )
                        } else mapped
                        it.copy(
                            contentState = TreatmentsContentState.Success(content),
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        if (page > 1 || refresh) it.copy(isRefreshing = false, isLoadingMore = false)
                        else it.copy(
                            contentState = TreatmentsContentState.Error("No pudimos cargar tus medicamentos. Intenta de nuevo."),
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
        }
    }
}
