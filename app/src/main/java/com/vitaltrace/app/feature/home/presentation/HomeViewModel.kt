package com.vitaltrace.app.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(createSampleState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<HomeUiEffect>(
        capacity = Channel.BUFFERED
    )

    val effects = effectChannel.receiveAsFlow()

    fun logout() {
        if (_uiState.value.isLoggingOut) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoggingOut = true,
                    errorMessage = null
                )
            }

            logoutUseCase()
                .onSuccess {
                    effectChannel.send(
                        HomeUiEffect.NavigateToLogin
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            errorMessage = throwable.message
                                ?: "Could not close the session."
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun retry() {
        _uiState.value = createSampleState()
    }

    private fun createSampleState(): HomeUiState {
        return HomeUiState(
            greeting = "Buenos días",
            patientName = "Ana Martínez",
            patientInitials = "AM",
            followUpStatus = FollowUpStatusUiModel(
                status = "En revisión",
                title = "Tu última medición fue enviada para revisión.",
                description = "Un profesional podrá revisarla pronto."
            ),
            nextAppointment = NextAppointmentUiModel(
                professionalName = "Dr. Carlos Ruiz",
                reason = "Control",
                date = "23 jul",
                time = "10:30 a. m.",
                status = "Programada"
            ),
            recentMeasurement = RecentMeasurementUiModel(
                value = "145/92",
                unit = "mmHg",
                date = "14 jul",
                chartValues = listOf(
                    0.46f,
                    0.58f,
                    0.52f,
                    0.68f,
                    0.61f,
                    0.93f
                )
            )
        )
    }
}
