package com.vitaltrace.app.feature.profile.presentation

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
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.profile.presentation.components.NotificationSettingsCard
import com.vitaltrace.app.feature.profile.presentation.components.ProfileErrorState
import com.vitaltrace.app.feature.profile.presentation.components.ProfileIdentityCard
import com.vitaltrace.app.feature.profile.presentation.components.ProfileInformationCard
import com.vitaltrace.app.feature.profile.presentation.components.RelativesAccessCard
import com.vitaltrace.app.feature.profile.presentation.components.ProfileLoadingState
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onRelativesClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        uiState = uiState,
        onHomeClick = onHomeClick,
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentsClick = onAppointmentsClick,
        onRelativesClick = onRelativesClick,
        onRetryClick = viewModel::retry,
        onMeasurementRemindersChange = viewModel::setMeasurementReminders,
        onAppointmentNotificationsChange = viewModel::setAppointmentNotifications,
        onEmailUpdatesChange = viewModel::setEmailUpdates
    )
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onRelativesClick: () -> Unit,
    onRetryClick: () -> Unit,
    onMeasurementRemindersChange: (Boolean) -> Unit,
    onAppointmentNotificationsChange: (Boolean) -> Unit,
    onEmailUpdatesChange: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = VitalTraceWarmBackground,
        bottomBar = {
            HomeBottomBar(
                selectedDestination = HomeBottomDestination.PROFILE,
                onHomeClick = onHomeClick,
                onMeasurementsClick = onMeasurementsClick,
                onAppointmentsClick = onAppointmentsClick,
                onProfileClick = {}
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> ProfileLoadingState(
                modifier = Modifier.padding(innerPadding)
            )
            uiState.errorMessage != null || uiState.user == null -> ProfileErrorState(
                message = uiState.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier.padding(innerPadding)
            )
            else -> ProfileBody(
                user = uiState.user,
                settings = uiState.notificationSettings,
                onMeasurementRemindersChange = onMeasurementRemindersChange,
                onAppointmentNotificationsChange = onAppointmentNotificationsChange,
                onEmailUpdatesChange = onEmailUpdatesChange,
                onRelativesClick = onRelativesClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun ProfileBody(
    user: ProfileUserUiModel,
    settings: NotificationSettingsUiModel,
    onMeasurementRemindersChange: (Boolean) -> Unit,
    onAppointmentNotificationsChange: (Boolean) -> Unit,
    onEmailUpdatesChange: (Boolean) -> Unit,
    onRelativesClick: () -> Unit,
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
                text = stringResource(R.string.profile_title),
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item { ProfileIdentityCard(user = user) }
        item { ProfileInformationCard(user = user) }
        item { RelativesAccessCard(onClick = onRelativesClick) }
        item {
            NotificationSettingsCard(
                settings = settings,
                onMeasurementRemindersChange = onMeasurementRemindersChange,
                onAppointmentNotificationsChange = onAppointmentNotificationsChange,
                onEmailUpdatesChange = onEmailUpdatesChange
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        ProfileContent(
            uiState = ProfileUiState(
                user = ProfileUserUiModel(
                    fullName = "Ana Martínez",
                    initials = "AM",
                    identifier = "VT-2026-014",
                    email = "ana.martinez@ejemplo.com",
                    phone = "+505 8888 0000"
                )
            ),
            onHomeClick = {},
            onMeasurementsClick = {},
            onAppointmentsClick = {},
            onRelativesClick = {},
            onRetryClick = {},
            onMeasurementRemindersChange = {},
            onAppointmentNotificationsChange = {},
            onEmailUpdatesChange = {}
        )
    }
}
