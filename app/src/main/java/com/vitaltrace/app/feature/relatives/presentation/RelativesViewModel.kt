package com.vitaltrace.app.feature.relatives.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.AuthorizePatientRelativeUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientRelativesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelativesViewModel @Inject constructor(
    private val getPatientRelatives: GetPatientRelativesUseCase,
    private val authorizePatientRelative: AuthorizePatientRelativeUseCase,
    private val mapper: RelativesMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(RelativesUiState())
    val uiState = _uiState.asStateFlow()
    private var loadRequest: Job? = null
    private var actionRequest: Job? = null

    init {
        loadRelatives()
    }

    fun retry() = loadRelatives()

    fun requestAction(relativeId: Long, action: RelativeAction) {
        if (actionRequest?.isActive == true) return
        _uiState.update { it.copy(confirmation = RelativeConfirmation(relativeId, action)) }
    }

    fun dismissConfirmation() {
        if (actionRequest?.isActive != true) {
            _uiState.update { it.copy(confirmation = null) }
        }
    }

    fun confirmAction() {
        val confirmation = _uiState.value.confirmation ?: return
        if (actionRequest?.isActive == true) return
        _uiState.update {
            it.copy(confirmation = null, actionInProgressId = confirmation.relativeId)
        }
        actionRequest = viewModelScope.launch {
            val result = authorizePatientRelative(confirmation.relativeId)
            result.onSuccess { updated ->
                val mapped = mapper.map(updated)
                _uiState.update { state ->
                    val content = state.content as? RelativesContentState.Success
                    state.copy(
                        content = content?.let {
                            RelativesContentState.Success(
                                it.relatives.map { item -> if (item.id == mapped.id) mapped else item }
                            )
                        } ?: state.content,
                        actionInProgressId = null,
                        message = "Acceso autorizado correctamente."
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        actionInProgressId = null,
                        message = "No pudimos actualizar el acceso. Intenta de nuevo."
                    )
                }
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun loadRelatives() {
        if (loadRequest?.isActive == true) return
        _uiState.update { it.copy(content = RelativesContentState.Loading) }
        loadRequest = viewModelScope.launch {
            getPatientRelatives()
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(content = RelativesContentState.Success(page.items.map(mapper::map)))
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            content = RelativesContentState.Error(
                                "No pudimos cargar tus familiares. Intenta de nuevo."
                            )
                        )
                    }
                }
        }
    }
}
