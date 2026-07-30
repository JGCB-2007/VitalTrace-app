package com.vitaltrace.app.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getPatientProfile: GetPatientProfileUseCase,
    private val mapper: ProfileMapper
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private var profileRequest: Job? = null

    init {
        loadProfile()
    }

    fun setMeasurementReminders(enabled: Boolean) {
        updateNotifications { it.copy(measurementRemindersEnabled = enabled) }
    }

    fun setAppointmentNotifications(enabled: Boolean) {
        updateNotifications { it.copy(appointmentNotificationsEnabled = enabled) }
    }

    fun setEmailUpdates(enabled: Boolean) {
        updateNotifications { it.copy(emailUpdatesEnabled = enabled) }
    }

    fun retry() = loadProfile()

    private fun loadProfile() {
        if (profileRequest?.isActive == true) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        profileRequest = viewModelScope.launch {
            getPatientProfile()
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(user = mapper.map(profile), isLoading = false)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            user = null,
                            isLoading = false,
                            errorMessage = "No pudimos cargar tu perfil. Intenta de nuevo."
                        )
                    }
                }
        }
    }

    private fun updateNotifications(
        update: (NotificationSettingsUiModel) -> NotificationSettingsUiModel
    ) {
        _uiState.update { state ->
            state.copy(notificationSettings = update(state.notificationSettings))
        }
    }
}
