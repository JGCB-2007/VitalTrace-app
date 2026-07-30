package com.vitaltrace.app.feature.home.presentation

data class HomeUiState(
    val contentState: HomeContentState = HomeContentState.Loading,
    val isLoggingOut: Boolean = false,
    val selectedBottomDestination: HomeBottomDestination = HomeBottomDestination.HOME
)

sealed interface HomeContentState {
    data object Loading : HomeContentState

    data class Success(
        val content: HomeContentUiModel
    ) : HomeContentState

    data class Error(
        val message: String
    ) : HomeContentState
}

data class HomeContentUiModel(
    val greeting: String,
    val patientName: String,
    val patientInitials: String,
    val followUpStatus: FollowUpStatusUiModel?,
    val nextAppointment: NextAppointmentUiModel?,
    val recentMeasurement: RecentMeasurementUiModel?
)

data class FollowUpStatusUiModel(
    val status: String,
    val title: String,
    val description: String
)

data class NextAppointmentUiModel(
    val id: Long,
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
