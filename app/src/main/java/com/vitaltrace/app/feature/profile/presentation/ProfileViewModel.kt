package com.vitaltrace.app.feature.profile.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(sampleProfileState())
    val uiState = _uiState.asStateFlow()

    fun setMeasurementReminders(enabled: Boolean) {
        updateNotifications { it.copy(measurementRemindersEnabled = enabled) }
    }

    fun setAppointmentNotifications(enabled: Boolean) {
        updateNotifications { it.copy(appointmentNotificationsEnabled = enabled) }
    }

    fun setEmailUpdates(enabled: Boolean) {
        updateNotifications { it.copy(emailUpdatesEnabled = enabled) }
    }

    fun retry() {
        _uiState.value = sampleProfileState()
    }

    private fun updateNotifications(
        update: (NotificationSettingsUiModel) -> NotificationSettingsUiModel
    ) {
        _uiState.update { state ->
            state.copy(notificationSettings = update(state.notificationSettings))
        }
    }
}

private fun sampleProfileState(): ProfileUiState {
    return ProfileUiState(
        user = ProfileUserUiModel(
            fullName = "Ana Martínez",
            initials = "AM",
            identifier = "VT-2026-014",
            email = "ana.martinez@ejemplo.com",
            phone = "+505 8888 0000"
        )
    )
}
