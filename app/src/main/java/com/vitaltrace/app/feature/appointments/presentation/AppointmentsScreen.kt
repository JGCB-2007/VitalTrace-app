package com.vitaltrace.app.feature.appointments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vitaltrace.app.ui.theme.SoraFontFamily
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentSection
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentsErrorState
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentsLoadingState
import com.vitaltrace.app.feature.appointments.presentation.components.FeaturedAppointmentCard
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.home.presentation.components.AdaptivePortalScaffold
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun AppointmentsScreen(
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    initialAppointmentId: Long? = null,
    onInitialDetailDismiss: () -> Unit = {},
    viewModel: AppointmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var initialDetailHandled by rememberSaveable(initialAppointmentId) { mutableStateOf(false) }

    LaunchedEffect(uiState.contentState, initialAppointmentId) {
        if (initialAppointmentId != null && !initialDetailHandled && uiState.contentState is AppointmentsContentState.Success) {
            viewModel.showAppointmentDetail(initialAppointmentId)
            initialDetailHandled = true
        }
    }

    AppointmentsContent(
        uiState = uiState,
        onRetryClick = viewModel::retry,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onQueryChange = viewModel::updateQuery,
        onHomeClick = onHomeClick,
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentClick = viewModel::showAppointmentDetail,
        onDismissAppointmentDetail = {
            if (initialAppointmentId != null) {
                onInitialDetailDismiss()
            } else {
                viewModel.dismissAppointmentDetail()
            }
        },
        onProfileClick = onProfileClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentsContent(
    uiState: AppointmentsUiState,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onQueryChange: (String) -> Unit,
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

    AdaptivePortalScaffold(
        selectedDestination = HomeBottomDestination.APPOINTMENTS,
        onHomeClick = onHomeClick,
        onMeasurementsClick = onMeasurementsClick,
        onAppointmentsClick = {},
        onProfileClick = onProfileClick,
        containerColor = MaterialTheme.colorScheme.background,
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
            is AppointmentsContentState.Success -> PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                AppointmentsBody(
                    content = uiState.filteredContent ?: contentState.content,
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    onAppointmentClick = onAppointmentClick,
                    onLoadMore = onLoadMore,
                    isLoadingMore = uiState.isLoadingMore,
                    modifier = Modifier.widthIn(max = 720.dp).align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun AppointmentsBody(
    content: AppointmentsContentUiModel,
    query: String,
    onQueryChange: (String) -> Unit,
    onAppointmentClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
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
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = SoraFontFamily,
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
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                placeholder = { Text("Buscar por profesional, especialidad o motivo") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )
        }
        if (content.currentPage < content.lastPage) {
            item(key = "load-more-${content.currentPage}") {
                LaunchedEffect(content.currentPage) { onLoadMore() }
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    if (isLoadingMore) CircularProgressIndicator()
                }
            }
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
            onRefresh = {},
            onLoadMore = {},
            onQueryChange = {},
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
