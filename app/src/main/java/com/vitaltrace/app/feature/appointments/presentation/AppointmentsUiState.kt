package com.vitaltrace.app.feature.appointments.presentation

data class AppointmentsUiState(
    val nextAppointment: AppointmentUiModel? = null,
    val upcomingAppointments: List<AppointmentUiModel> = emptyList(),
    val previousAppointments: List<AppointmentUiModel> = emptyList(),
    val selectedAppointmentDetail: AppointmentDetailUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class AppointmentDetailUiModel(
    val id: String,
    val professionalName: String,
    val professionalInitials: String,
    val specialty: String,
    val reason: String,
    val date: String,
    val time: String,
    val status: AppointmentStatus
)

data class AppointmentUiModel(
    val id: String,
    val professionalName: String,
    val reason: String,
    val date: String,
    val time: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus {
    SCHEDULED,
    COMPLETED
}
