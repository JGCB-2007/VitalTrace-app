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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    viewModel: AppointmentsViewModel = hiltViewModel()
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
    onAppointmentClick: (Long) -> Unit,
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
        when (val contentState = uiState.contentState) {
            AppointmentsContentState.Loading -> AppointmentsLoadingState(
                modifier = Modifier.padding(innerPadding)
            )
            is AppointmentsContentState.Error -> AppointmentsErrorState(
                message = contentState.message,
                onRetryClick = onRetryClick,
                modifier = Modifier.padding(innerPadding)
            )
            is AppointmentsContentState.Success -> AppointmentsBody(
                content = contentState.content,
                onAppointmentClick = onAppointmentClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun AppointmentsBody(
    content: AppointmentsContentUiModel,
    onAppointmentClick: (Long) -> Unit,
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
        content.nextAppointment?.let { appointment ->
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
                appointments = content.upcomingAppointments,
                emptyMessage = stringResource(R.string.appointments_empty_upcoming),
                onAppointmentClick = onAppointmentClick
            )
        }
        item {
            AppointmentSection(
                title = stringResource(R.string.appointments_previous),
                appointments = content.previousAppointments,
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
        contentState = AppointmentsContentState.Success(
            AppointmentsContentUiModel(
        nextAppointment = AppointmentUiModel(
            id = 1,
            professionalName = "Dr. Carlos Ruiz",
            professionalInitials = "CR",
            specialty = "Medicina interna",
            reason = "Control de presión arterial",
            date = "23 jul 2026",
            time = "10:30 a. m.",
            scheduledAt = "2026-07-23 10:30:00",
            status = AppointmentStatus.SCHEDULED
        ),
        upcomingAppointments = listOf(
            AppointmentUiModel(
                id = 2,
                professionalName = "Dra. Elena Ortiz",
                professionalInitials = "EO",
                specialty = "Nutrición",
                reason = "Nutrición",
                date = "5 ago",
                time = "9:00 a. m.",
                scheduledAt = "2026-08-05 09:00:00",
                status = AppointmentStatus.SCHEDULED
            )
        ),
        previousAppointments = listOf(
            AppointmentUiModel(
                id = 3,
                professionalName = "Dr. Carlos Ruiz",
                professionalInitials = "CR",
                specialty = "Medicina interna",
                reason = "Control",
                date = "25 jun",
                time = "10:30 a. m.",
                scheduledAt = "2026-06-25 10:30:00",
                status = AppointmentStatus.ATTENDED
            )
        ),
                currentPage = 1,
                lastPage = 1
            )
        )
    )
}
