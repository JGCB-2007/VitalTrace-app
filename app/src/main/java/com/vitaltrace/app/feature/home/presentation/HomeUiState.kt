package com.vitaltrace.app.feature.home.presentation

data class HomeUiState(
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
    val greeting: String = "",
    val patientName: String = "",
    val patientInitials: String = "",
    val followUpStatus: FollowUpStatusUiModel? = null,
    val nextAppointment: NextAppointmentUiModel? = null,
    val recentMeasurement: RecentMeasurementUiModel? = null,
    val selectedBottomDestination: HomeBottomDestination =
        HomeBottomDestination.HOME
) {
    val hasContent: Boolean
        get() = followUpStatus != null ||
            nextAppointment != null ||
            recentMeasurement != null
}

data class FollowUpStatusUiModel(
    val status: String,
    val title: String,
    val description: String
)

data class NextAppointmentUiModel(
    val professionalName: String,
    val reason: String,
    val date: String,
    val time: String,
    val status: String
)

data class RecentMeasurementUiModel(
    val value: String,
    val unit: String,
    val date: String,
    val chartValues: List<Float>
)

enum class HomeBottomDestination {
    HOME,
    MEASUREMENTS,
    APPOINTMENTS,
    PROFILE
}
