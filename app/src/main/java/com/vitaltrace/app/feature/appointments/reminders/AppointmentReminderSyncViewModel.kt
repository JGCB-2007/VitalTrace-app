package com.vitaltrace.app.feature.appointments.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientAppointmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentReminderSyncViewModel @Inject constructor(
    getPatientAppointments: GetPatientAppointmentsUseCase
) : ViewModel() {
    init {
        viewModelScope.launch {
            // Failure intentionally leaves all previously scheduled work untouched.
            getPatientAppointments()
        }
    }
}