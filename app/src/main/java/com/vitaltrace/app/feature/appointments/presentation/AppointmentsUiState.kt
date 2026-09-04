package com.vitaltrace.app.feature.appointments.presentation

data class AppointmentsUiState(
    val contentState: AppointmentsContentState = AppointmentsContentState.Loading,
    val selectedAppointmentDetail: AppointmentDetailUiModel? = null
)

sealed interface AppointmentsContentState {
    data object Loading : AppointmentsContentState

    data class Success(
        val content: AppointmentsContentUiModel
    ) : AppointmentsContentState

    data class Error(
        val message: String
    ) : AppointmentsContentState
}

data class AppointmentsContentUiModel(
    val nextAppointment: AppointmentUiModel?,
    val upcomingAppointments: List<AppointmentUiModel>,
    val previousAppointments: List<AppointmentUiModel>,
    val currentPage: Int,
    val lastPage: Int
)

data class AppointmentDetailUiModel(
    val id: Long,
    val professionalName: String,
    val professionalInitials: String,
    val specialty: String,
    val reason: String,
    val date: String,
    val time: String,
    val durationMinutes: Int = 0,
    val status: AppointmentStatus,
    val contextName: String? = null
)

data class AppointmentUiModel(
    val id: Long,
    val professionalName: String,
    val professionalInitials: String,
    val specialty: String,
    val reason: String,
    val date: String,
    val time: String,
    val scheduledAt: String,
    val durationMinutes: Int = 0,
    val status: AppointmentStatus
) {
    fun toDetail(): AppointmentDetailUiModel = AppointmentDetailUiModel(
        id = id,
        professionalName = professionalName,
        professionalInitials = professionalInitials,
        specialty = specialty,
        reason = reason,
        date = date,
        time = time,
        durationMinutes = durationMinutes,
        status = status
    )
}

enum class AppointmentStatus(val isUpcoming: Boolean) {
    SCHEDULED(true),
    CONFIRMED(true),
    ATTENDED(false),
    CANCELLED(false),
    NO_SHOW(false),
    UNKNOWN(false);

    companion object {
        fun fromApiValue(value: String): AppointmentStatus {
            return entries.firstOrNull { it.name == value.trim().uppercase() } ?: UNKNOWN
        }
    }
}
