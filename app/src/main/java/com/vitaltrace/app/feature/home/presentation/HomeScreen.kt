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
        onProfileClick = onProfileClick
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
    onProfileClick: () -> Unit
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
        when {
            uiState.isLoading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            uiState.errorMessage != null && !uiState.hasContent -> {
                HomeErrorState(
                    message = uiState.errorMessage,
                    onRetryClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            else -> HomeBody(
                uiState = uiState,
                onLogoutClick = onLogoutClick,
                onRegisterMeasurementClick = onRegisterMeasurementClick,
                onAppointmentDetailClick = onAppointmentDetailClick,
                onPressureHistoryClick = onPressureHistoryClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun HomeBody(
    uiState: HomeUiState,
    onLogoutClick: () -> Unit,
    onRegisterMeasurementClick: () -> Unit,
    onAppointmentDetailClick: () -> Unit,
    onPressureHistoryClick: () -> Unit,
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
                greeting = uiState.greeting,
                patientName = uiState.patientName,
                patientInitials = uiState.patientInitials,
                isLoggingOut = uiState.isLoggingOut,
                onLogoutClick = onLogoutClick
            )
        }
        item {
            FollowUpStatusCard(
                status = uiState.followUpStatus,
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
                appointment = uiState.nextAppointment,
                onDetailClick = onAppointmentDetailClick,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
        item {
            RecentPressureCard(
                measurement = uiState.recentMeasurement,
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
            onProfileClick = {}
        )
    }
}

private fun previewHomeState(): HomeUiState {
    return HomeUiState(
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
}
