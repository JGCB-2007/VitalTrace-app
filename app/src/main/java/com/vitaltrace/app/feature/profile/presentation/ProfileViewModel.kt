package com.vitaltrace.app.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientProfileUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientAppointmentsUseCase
import com.vitaltrace.app.feature.appointments.reminders.AppointmentReminderScheduler
import com.vitaltrace.app.feature.measurements.reminders.MeasurementReminderScheduler
import com.vitaltrace.app.feature.profile.data.NotificationPreferencesStore
import com.vitaltrace.app.core.settings.AppPreferencesStore
import com.vitaltrace.app.core.settings.ThemePreference
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
    private val mapper: ProfileMapper,
    private val preferencesStore: NotificationPreferencesStore,
    private val appointmentReminderScheduler: AppointmentReminderScheduler,
    private val measurementReminderScheduler: MeasurementReminderScheduler,
    private val getPatientAppointments: GetPatientAppointmentsUseCase,
    private val appPreferencesStore: AppPreferencesStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private var profileRequest: Job? = null

    init {
        viewModelScope.launch {
            preferencesStore.settings.collect { settings ->
                _uiState.update { it.copy(notificationSettings = settings) }
                measurementReminderScheduler.apply(settings)
                if (!settings.appointmentNotificationsEnabled) appointmentReminderScheduler.cancelAll()
            }
        }
        viewModelScope.launch {
            appPreferencesStore.preferences.collect { preferences ->
                _uiState.update {
                    it.copy(
                        appSettings = AppSettingsUiModel(
                            theme = preferences.theme,
                            secureScreenEnabled = preferences.secureScreenEnabled,
                            avatarUri = preferences.avatarUri,
                            largeTextEnabled = preferences.largeTextEnabled
                        )
                    )
                }
            }
        }
        loadProfile()
    }

    fun setMeasurementReminders(enabled: Boolean) {
        updateNotifications { it.copy(measurementRemindersEnabled = enabled) }
        viewModelScope.launch { preferencesStore.setMeasurementReminders(enabled) }
    }

    fun setAppointmentNotifications(enabled: Boolean) {
        updateNotifications { it.copy(appointmentNotificationsEnabled = enabled) }
        if (!enabled) appointmentReminderScheduler.cancelAll()
        viewModelScope.launch {
            preferencesStore.setAppointmentNotifications(enabled)
            if (enabled) getPatientAppointments(forceRefresh = true)
        }
    }

    fun setReminderTime(hour: Int, minute: Int) = viewModelScope.launch {
        preferencesStore.setReminderTime(hour, minute)
    }

    fun toggleReminderDay(day: Int) {
        val current = _uiState.value.notificationSettings.reminderDays
        val next = if (day in current && current.size > 1) current - day else current + day
        viewModelScope.launch { preferencesStore.setReminderDays(next) }
    }

    fun setSnoozeMinutes(minutes: Int) = viewModelScope.launch {
        preferencesStore.setSnoozeMinutes(minutes)
    }

    fun setTheme(theme: ThemePreference) = viewModelScope.launch {
        appPreferencesStore.setTheme(theme)
    }

    fun setSecureScreen(enabled: Boolean) = viewModelScope.launch {
        appPreferencesStore.setSecureScreen(enabled)
    }

    fun setAvatarUri(uri: String?) = viewModelScope.launch {
        appPreferencesStore.setAvatarUri(uri)
    }

    fun setLargeText(enabled: Boolean) = viewModelScope.launch {
        appPreferencesStore.setLargeText(enabled)
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
