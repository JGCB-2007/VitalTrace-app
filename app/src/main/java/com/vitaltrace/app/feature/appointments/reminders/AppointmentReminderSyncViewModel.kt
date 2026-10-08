package com.vitaltrace.app.feature.appointments.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientAppointmentsUseCase
import com.vitaltrace.app.feature.profile.data.NotificationPreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class AppointmentReminderSyncViewModel @Inject constructor(
    getPatientAppointments: GetPatientAppointmentsUseCase,
    preferencesStore: NotificationPreferencesStore
) : ViewModel() {
    private val _notificationsEnabled = MutableStateFlow<Boolean?>(null)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            val enabled = preferencesStore.settings.first().appointmentNotificationsEnabled
            _notificationsEnabled.value = enabled
            if (enabled) {
                // Failure intentionally leaves all previously scheduled work untouched.
                getPatientAppointments()
            }
        }
    }
}
