package com.vitaltrace.app.feature.appointments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentSection
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentsErrorState
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentsLoadingState
import com.vitaltrace.app.feature.appointments.presentation.components.FeaturedAppointmentCard
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun AppointmentsScreen(
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: AppointmentsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppointmentsContent(
        uiState = uiState,
        onRetryClick = viewModel::retry,
        onHomeClick = onHomeClick,
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentClick = viewModel::showAppointmentDetail,
        onDismissAppointmentDetail = viewModel::dismissAppointmentDetail,
        onProfileClick = onProfileClick
    )
}

@Composable
private fun AppointmentsContent(
    uiState: AppointmentsUiState,
    onRetryClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentClick: (String) -> Unit,
    onDismissAppointmentDetail: () -> Unit,
    onProfileClick: () -> Unit
) {
    uiState.selectedAppointmentDetail?.let { detail ->
        AppointmentDetailSheet(
            detail = detail,
            onDismiss = onDismissAppointmentDetail
        )
    }

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        bottomBar = {
            HomeBottomBar(
                selectedDestination = HomeBottomDestination.APPOINTMENTS,
                onHomeClick = onHomeClick,
                onMeasurementsClick = onMeasurementsClick,
                onAppointmentsClick = {},
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> AppointmentsLoadingState(
                modifier = Modifier.padding(innerPadding)
            )
            uiState.errorMessage != null -> AppointmentsErrorState(
                message = uiState.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier.padding(innerPadding)
            )
            else -> AppointmentsBody(
                uiState = uiState,
                onAppointmentClick = onAppointmentClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun AppointmentsBody(
    uiState: AppointmentsUiState,
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 30.dp,
            end = 24.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.appointments_title),
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }
        uiState.nextAppointment?.let { appointment ->
            item {
                FeaturedAppointmentCard(
                    appointment = appointment,
                    onClick = { onAppointmentClick(appointment.id) }
                )
            }
        }
        item {
            AppointmentSection(
                title = stringResource(R.string.appointments_upcoming),
                appointments = uiState.upcomingAppointments,
                emptyMessage = stringResource(R.string.appointments_empty_upcoming),
                onAppointmentClick = onAppointmentClick
            )
        }
        item {
            AppointmentSection(
                title = stringResource(R.string.appointments_previous),
                appointments = uiState.previousAppointments,
                emptyMessage = stringResource(R.string.appointments_empty_previous),
                onAppointmentClick = onAppointmentClick
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AppointmentsScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        AppointmentsContent(
            uiState = previewAppointmentsState(),
            onRetryClick = {},
            onHomeClick = {},
            onMeasurementsClick = {},
            onAppointmentClick = {},
            onDismissAppointmentDetail = {},
            onProfileClick = {}
        )
    }
}

private fun previewAppointmentsState(): AppointmentsUiState {
    return AppointmentsUiState(
        nextAppointment = AppointmentUiModel(
            id = "preview-next",
            professionalName = "Dr. Carlos Ruiz",
            reason = "Control de presión arterial",
            date = "23 jul 2026",
            time = "10:30 a. m.",
            status = AppointmentStatus.SCHEDULED
        ),
        upcomingAppointments = listOf(
            AppointmentUiModel(
                id = "preview-upcoming",
                professionalName = "Dra. Elena Ortiz",
                reason = "Nutrición",
                date = "5 ago",
                time = "9:00 a. m.",
                status = AppointmentStatus.SCHEDULED
            )
        ),
        previousAppointments = listOf(
            AppointmentUiModel(
                id = "preview-previous",
                professionalName = "Dr. Carlos Ruiz",
                reason = "Control",
                date = "25 jun",
                time = "10:30 a. m.",
                status = AppointmentStatus.COMPLETED
            )
        )
    )
}
