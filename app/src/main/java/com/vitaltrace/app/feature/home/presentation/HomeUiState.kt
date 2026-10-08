package com.vitaltrace.app.feature.home.presentation

data class HomeUiState(
    val contentState: HomeContentState = HomeContentState.Loading,
    val isLoggingOut: Boolean = false,
    val isRefreshing: Boolean = false,
    val unreadNotificationsCount: Int = 0,
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
    val recentMeasurement: RecentMeasurementUiModel?,
    val healthStatus: HealthStatusUiModel = HealthStatusUiModel(),
    val activeTreatmentsCount: Int = 0
)

data class HealthStatusUiModel(
    val level: HealthStatusLevel = HealthStatusLevel.STABLE,
    val title: String = "Seguimiento estable",
    val description: String = "No hay alertas abiertas en este momento.",
    val openAlerts: Int = 0
)

enum class HealthStatusLevel { STABLE, ATTENTION, CRITICAL }

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
    val typeName: String,
    val value: String,
    val unit: String,
    val date: String,
    val trendPoints: List<MeasurementTrendPoint>
)

data class MeasurementTrendPoint(
    val normalizedValue: Float,
    val displayValue: String,
    val dateLabel: String
)

enum class HomeBottomDestination {
    HOME,
    MEASUREMENTS,
    APPOINTMENTS,
    PROFILE
}
