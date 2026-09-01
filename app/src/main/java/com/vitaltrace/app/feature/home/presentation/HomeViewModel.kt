package com.vitaltrace.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientMeasurementsUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientSummaryUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetUnreadNotificationsCountUseCase
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
    private val getPatientMeasurements: GetPatientMeasurementsUseCase,
    private val getUnreadNotificationsCount: GetUnreadNotificationsCountUseCase,
    private val summaryMapper: HomeSummaryMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<HomeUiEffect>(capacity = Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    private var summaryRequest: Job? = null
    private var notificationsCountRequest: Job? = null

    init {
        loadSummary()
        refreshUnreadNotificationsCount()
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
        refreshUnreadNotificationsCount()
    }

    fun refreshAfterMeasurementCreated() {
        summaryRequest?.cancel()
        summaryRequest = null
        loadSummary()
    }

    fun refreshUnreadNotificationsCount() {
        if (notificationsCountRequest?.isActive == true) return
        notificationsCountRequest = viewModelScope.launch {
            getUnreadNotificationsCount().onSuccess { count ->
                _uiState.update { it.copy(unreadNotificationsCount = count.coerceAtLeast(0)) }
            }
        }
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
                    if (summary.latestMeasurements.isNotEmpty()) {
                        getPatientMeasurements().onSuccess { page ->
                            _uiState.update {
                                it.copy(
                                    contentState = HomeContentState.Success(
                                        summaryMapper.map(summary, page.items)
                                    )
                                )
                            }
                        }
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
