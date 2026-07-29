package com.vitaltrace.app.feature.appointments.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppointmentsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(sampleAppointmentsState())
    val uiState = _uiState.asStateFlow()

    fun retry() {
        _uiState.value = sampleAppointmentsState()
    }
}

private fun sampleAppointmentsState(): AppointmentsUiState {
    return AppointmentsUiState(
        nextAppointment = AppointmentUiModel(
            id = "next-appointment",
            professionalName = "Dr. Carlos Ruiz",
            reason = "Control de presión arterial",
            date = "23 jul 2026",
            time = "10:30 a. m.",
            status = AppointmentStatus.SCHEDULED
        ),
        upcomingAppointments = listOf(
            AppointmentUiModel(
                id = "upcoming-appointment",
                professionalName = "Dra. Elena Ortiz",
                reason = "Nutrición",
                date = "5 ago",
                time = "9:00 a. m.",
                status = AppointmentStatus.SCHEDULED
            )
        ),
        previousAppointments = listOf(
            AppointmentUiModel(
                id = "previous-appointment-1",
                professionalName = "Dr. Carlos Ruiz",
                reason = "Control",
                date = "25 jun",
                time = "10:30 a. m.",
                status = AppointmentStatus.COMPLETED
            ),
            AppointmentUiModel(
                id = "previous-appointment-2",
                professionalName = "Dr. Carlos Ruiz",
                reason = "Control",
                date = "28 may",
                time = "10:30 a. m.",
                status = AppointmentStatus.COMPLETED
            )
        )
    )
}
