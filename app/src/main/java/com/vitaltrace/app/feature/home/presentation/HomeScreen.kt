package com.vitaltrace.app.feature.home.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.home.presentation.components.FollowUpStatusCard
import com.vitaltrace.app.feature.home.presentation.components.HomeBackground
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.home.presentation.components.HomeHeader
import com.vitaltrace.app.feature.home.presentation.components.LoadingState
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.feature.home.presentation.components.RecentPressureCard
import com.vitaltrace.app.feature.home.presentation.components.RegisterMeasurementButton
import com.vitaltrace.app.feature.home.presentation.components.TreatmentsAccessCard
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onLogoutSuccess: () -> Unit,
    onRegisterMeasurementClick: () -> Unit = {},
    onAppointmentDetailClick: () -> Unit = {},
    onPressureHistoryClick: () -> Unit = {},
    onMeasurementsClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTreatmentsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        onRegisterMeasurementClick = onRegisterMeasurementClick,
        onAppointmentDetailClick = onAppointmentDetailClick,
        onPressureHistoryClick = onPressureHistoryClick,
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick,
        onTreatmentsClick = onTreatmentsClick
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onLogoutClick: () -> Unit,
    onRetryClick: () -> Unit,
    onRegisterMeasurementClick: () -> Unit,
    onAppointmentDetailClick: () -> Unit,
    onPressureHistoryClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onTreatmentsClick: () -> Unit
) {
    Scaffold(
        containerColor = HomeBackground,
        bottomBar = {
            HomeBottomBar(
                selectedDestination = uiState.selectedBottomDestination,
                onHomeClick = {},
                onMeasurementsClick = onMeasurementsClick,
                onAppointmentsClick = onAppointmentsClick,
                onProfileClick = onProfileClick
            )
        }
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
            is HomeContentState.Success -> HomeBody(
                content = contentState.content,
                isLoggingOut = uiState.isLoggingOut,
                onLogoutClick = onLogoutClick,
                onRegisterMeasurementClick = onRegisterMeasurementClick,
                onAppointmentDetailClick = onAppointmentDetailClick,
                onPressureHistoryClick = onPressureHistoryClick,
                onTreatmentsClick = onTreatmentsClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun HomeBody(
    content: HomeContentUiModel,
    isLoggingOut: Boolean,
    onLogoutClick: () -> Unit,
    onRegisterMeasurementClick: () -> Unit,
    onAppointmentDetailClick: () -> Unit,
    onPressureHistoryClick: () -> Unit,
    onTreatmentsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 30.dp,
            end = 24.dp,
            bottom = 28.dp
        )
    ) {
        item {
            HomeHeader(
                greeting = content.greeting,
                patientName = content.patientName,
                patientInitials = content.patientInitials,
                isLoggingOut = isLoggingOut,
                onLogoutClick = onLogoutClick
            )
        }
        item {
            FollowUpStatusCard(
                status = content.followUpStatus,
                modifier = Modifier.padding(top = 28.dp)
            )
        }
        item {
            RegisterMeasurementButton(
                onClick = onRegisterMeasurementClick,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        item {
            NextAppointmentCard(
                appointment = content.nextAppointment,
                onDetailClick = onAppointmentDetailClick,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
        item {
            TreatmentsAccessCard(
                onClick = onTreatmentsClick,
                modifier = Modifier.padding(top = 18.dp)
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
            onRegisterMeasurementClick = {},
            onAppointmentDetailClick = {},
            onPressureHistoryClick = {},
            onMeasurementsClick = {},
            onAppointmentsClick = {},
            onProfileClick = {},
            onTreatmentsClick = {}
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
            professionalName = "Dr. Carlos Ruiz",
            reason = "Control",
            date = "23 jul",
            time = "10:30 a. m.",
            status = "Programada"
        ),
        recentMeasurement = RecentMeasurementUiModel(
            value = "145/92",
            unit = "mmHg",
            date = "14 jul",
            chartValues = listOf(
                0.46f,
                0.58f,
                0.52f,
                0.68f,
                0.61f,
                0.93f
            )
        )
            )
        )
    )
}
