package com.vitaltrace.app.feature.home.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.appointments.reminders.AppointmentNotificationPermissionEffect
import com.vitaltrace.app.feature.home.presentation.components.FollowUpStatusCard
import com.vitaltrace.app.feature.home.presentation.components.HomeBackground
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.home.presentation.components.HomeHeader
import com.vitaltrace.app.feature.home.presentation.components.LoadingState
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.feature.home.presentation.components.RecentPressureCard
import com.vitaltrace.app.feature.home.presentation.components.HealthOverviewCard
import com.vitaltrace.app.feature.home.presentation.components.AdaptivePortalScaffold
import com.vitaltrace.app.feature.home.presentation.components.TodayOverviewCard
import com.vitaltrace.app.core.presentation.components.OfflineStatusBanner
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onLogoutSuccess: () -> Unit,
    onRegisterMeasurementClick: () -> Unit = {},
    onAppointmentDetailClick: (Long) -> Unit = {},
    onPressureHistoryClick: () -> Unit = {},
    onMeasurementsClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTimelineClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppointmentNotificationPermissionEffect()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                HomeUiEffect.NavigateToLogin -> onLogoutSuccess()
            }
        }
    }

    HomeContent(
        uiState = uiState,
        onLogoutClick = viewModel::logout,
        onRetryClick = viewModel::retry,
        onRefresh = viewModel::refresh,
        onRegisterMeasurementClick = onRegisterMeasurementClick,
        onAppointmentDetailClick = onAppointmentDetailClick,
        onPressureHistoryClick = onPressureHistoryClick,
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick,
        onNotificationsClick = onNotificationsClick,
        onTimelineClick = onTimelineClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onLogoutClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    onRegisterMeasurementClick: () -> Unit,
    onAppointmentDetailClick: (Long) -> Unit,
    onPressureHistoryClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onTimelineClick: () -> Unit
) {
    AdaptivePortalScaffold(
        selectedDestination = uiState.selectedBottomDestination,
        onHomeClick = {},
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick,
        containerColor = HomeBackground,
    ) { innerPadding ->
        when (val contentState = uiState.contentState) {
            HomeContentState.Loading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is HomeContentState.Error -> HomeErrorState(
                message = contentState.message,
                onRetryClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            is HomeContentState.Success -> PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                HomeBody(
                    content = contentState.content,
                    isLoggingOut = uiState.isLoggingOut,
                    onLogoutClick = onLogoutClick,
                    onRegisterMeasurementClick = onRegisterMeasurementClick,
                    onAppointmentDetailClick = onAppointmentDetailClick,
                    onPressureHistoryClick = onPressureHistoryClick,
                    unreadNotificationsCount = uiState.unreadNotificationsCount,
                    onNotificationsClick = onNotificationsClick,
                    onTimelineClick = onTimelineClick,
                    modifier = Modifier.widthIn(max = 720.dp).align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun HomeBody(
    content: HomeContentUiModel,
    isLoggingOut: Boolean,
    onLogoutClick: () -> Unit,
    onRegisterMeasurementClick: () -> Unit,
    onAppointmentDetailClick: (Long) -> Unit,
    onPressureHistoryClick: () -> Unit,
    unreadNotificationsCount: Int,
    onNotificationsClick: () -> Unit,
    onTimelineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 30.dp,
            end = 24.dp,
            bottom = 30.dp
        )
    )  {
        item { OfflineStatusBanner() }
        item {
            HomeHeader(
                greeting = content.greeting,
                patientName = content.patientName,
                patientInitials = content.patientInitials,
                isLoggingOut = isLoggingOut,
                onLogoutClick = onLogoutClick,
                unreadNotificationsCount = unreadNotificationsCount,
                onNotificationsClick = onNotificationsClick
            )
        }
        item {
            TodayOverviewCard(
                appointment = content.nextAppointment,
                measurement = content.recentMeasurement,
                activeTreatmentsCount = content.activeTreatmentsCount,
                onRegisterMeasurement = onRegisterMeasurementClick,
                onAppointmentClick = {
                    content.nextAppointment?.let { onAppointmentDetailClick(it.id) }
                },
                modifier = Modifier.padding(top = 18.dp)
            )
        }
        item {
            HealthOverviewCard(
                status = content.healthStatus,
                activeTreatmentsCount = content.activeTreatmentsCount,
                onTimelineClick = onTimelineClick,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
        item {
            FollowUpStatusCard(
                status = content.followUpStatus,
                modifier = Modifier.padding(top = 28.dp)
            )
        }
        item {
            NextAppointmentCard(
                appointment = content.nextAppointment,
                onDetailClick = { content.nextAppointment?.let { onAppointmentDetailClick(it.id) } },
                modifier = Modifier.padding(top = 30.dp)
            )
        }
        item {
            RecentPressureCard(
                measurement = content.recentMeasurement,
                onHistoryClick = onPressureHistoryClick,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        HomeContent(
            uiState = previewHomeState(),
            onLogoutClick = {},
            onRetryClick = {},
            onRefresh = {},
            onRegisterMeasurementClick = {},
            onAppointmentDetailClick = {},
            onPressureHistoryClick = {},
            onMeasurementsClick = {},
            onAppointmentsClick = {},
            onProfileClick = {},
            onNotificationsClick = {},
            onTimelineClick = {}
        )
    }
}

private fun previewHomeState(): HomeUiState {
    return HomeUiState(
        contentState = HomeContentState.Success(
            HomeContentUiModel(
        greeting = "Buenos días",
        patientName = "Ana Martínez",
        patientInitials = "AM",
        followUpStatus = FollowUpStatusUiModel(
            status = "En revisión",
            title = "Tu última medición fue enviada para revisión.",
            description = "Un profesional podrá revisarla pronto."
        ),
        nextAppointment = NextAppointmentUiModel(
            id = 25,
            professionalName = "Dr. Carlos Ruiz",
            reason = "Control",
            date = "23 jul",
            time = "10:30 a. m.",
            status = "Programada"
        ),
        recentMeasurement = RecentMeasurementUiModel(
            typeName = "Presión arterial sistólica",
            value = "145/92",
            unit = "mmHg",
            date = "14 jul",
            trendPoints = listOf(
                MeasurementTrendPoint(0.46f, "128", "08/07"),
                MeasurementTrendPoint(0.58f, "132", "09/07"),
                MeasurementTrendPoint(0.52f, "130", "10/07"),
                MeasurementTrendPoint(0.68f, "136", "11/07"),
                MeasurementTrendPoint(0.61f, "133", "12/07"),
                MeasurementTrendPoint(0.93f, "145", "14/07")
            )
        )
            )
        )
    )
}
