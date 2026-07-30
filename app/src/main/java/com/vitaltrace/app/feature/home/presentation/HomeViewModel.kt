package com.vitaltrace.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getPatientSummary: GetPatientSummaryUseCase,
    private val summaryMapper: HomeSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<HomeUiEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    private var summaryRequest: Job? = null

    init {
        loadSummary()
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            logoutUseCase()
                .onSuccess {
                    effectChannel.send(HomeUiEffect.NavigateToLogin)
                }
                .onFailure {
                    _uiState.update { state -> state.copy(isLoggingOut = false) }
                }
        }
    }

    fun retry() {
        loadSummary()
    }

    private fun loadSummary() {
        if (summaryRequest?.isActive == true) return

        _uiState.update { it.copy(contentState = HomeContentState.Loading) }
        summaryRequest = viewModelScope.launch {
            getPatientSummary()
                .onSuccess { summary ->
                    _uiState.update {
                        it.copy(
                            contentState = HomeContentState.Success(
                                summaryMapper.map(summary)
                            )
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            contentState = HomeContentState.Error(
                                "No pudimos cargar tu información. Intenta de nuevo."
                            )
                        )
                    }
                }
        }
    }
}
