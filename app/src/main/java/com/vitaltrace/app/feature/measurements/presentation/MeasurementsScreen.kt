package com.vitaltrace.app.feature.measurements.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementHistoryCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementFilterBar
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsEmptyState
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsErrorState
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsHeader
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsLoadingState
import com.vitaltrace.app.feature.measurements.presentation.detail.MeasurementDetailSheet
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun MeasurementsScreen(
    onHomeClick: () -> Unit,
    onAddMeasurementClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: MeasurementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MeasurementsContent(
        uiState = uiState,
        onRetryClick = viewModel::retry,
        onFilterSelected = viewModel::selectFilter,
        onMeasurementClick = viewModel::showMeasurementDetail,
        onDismissMeasurementDetail = viewModel::dismissMeasurementDetail,
        onHomeClick = onHomeClick,
        onAddMeasurementClick = onAddMeasurementClick,
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick
    )
}

@Composable
private fun MeasurementsContent(
    uiState: MeasurementsUiState,
    onRetryClick: () -> Unit,
    onFilterSelected: (MeasurementFilter) -> Unit,
    onMeasurementClick: (Long) -> Unit,
    onDismissMeasurementDetail: () -> Unit,
    onHomeClick: () -> Unit,
    onAddMeasurementClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    uiState.selectedMeasurementDetail?.let { detail ->
        MeasurementDetailSheet(detail = detail, onDismiss = onDismissMeasurementDetail)
    }

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        bottomBar = {
            HomeBottomBar(
                selectedDestination = HomeBottomDestination.MEASUREMENTS,
                onHomeClick = onHomeClick,
                onMeasurementsClick = {},
                onAppointmentsClick = onAppointmentsClick,
                onProfileClick = onProfileClick
            )
        },
        floatingActionButton = {
            if (uiState.contentState is MeasurementsContentState.Success) {
                FloatingActionButton(
                    onClick = onAddMeasurementClick,
                    containerColor = VitalTraceNavy,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.measurements_add)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (val contentState = uiState.contentState) {
            MeasurementsContentState.Loading -> MeasurementsLoadingState(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            )
            is MeasurementsContentState.Error -> MeasurementsErrorState(
                message = contentState.message,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            )
            is MeasurementsContentState.Success -> MeasurementsBody(
                content = contentState.content,
                selectedFilter = uiState.selectedFilter,
                visibleMeasurements = uiState.visibleMeasurements,
                onFilterSelected = onFilterSelected,
                onAddMeasurementClick = onAddMeasurementClick,
                onMeasurementClick = onMeasurementClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun MeasurementsBody(
    content: MeasurementsContentUiModel,
    selectedFilter: MeasurementFilter,
    visibleMeasurements: List<MeasurementUiModel>,
    onFilterSelected: (MeasurementFilter) -> Unit,
    onAddMeasurementClick: () -> Unit,
    onMeasurementClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp, 30.dp, 24.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { MeasurementsHeader(onAddMeasurementClick = onAddMeasurementClick) }
        item {
            MeasurementFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }
        if (selectedFilter == MeasurementFilter.ALL) content.latestMeasurement?.let { measurement ->
            item {
                LatestMeasurementCard(
                    measurement = measurement,
                    onClick = { onMeasurementClick(measurement.id) }
                )
            }
        }
        if (
            visibleMeasurements.isEmpty() &&
            (selectedFilter != MeasurementFilter.ALL || content.latestMeasurement == null)
        ) {
            item { MeasurementsEmptyState(onAddMeasurementClick = onAddMeasurementClick) }
        } else if (visibleMeasurements.isNotEmpty()) {
            item {
                MeasurementHistoryCard(
                    measurements = visibleMeasurements,
                    onMeasurementClick = onMeasurementClick
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MeasurementsScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        MeasurementsContent(
            uiState = MeasurementsUiState(
                contentState = MeasurementsContentState.Success(
                    MeasurementsContentUiModel(null, emptyList(), 1, 1)
                )
            ),
            onRetryClick = {},
            onFilterSelected = {},
            onMeasurementClick = {},
            onDismissMeasurementDetail = {},
            onHomeClick = {},
            onAddMeasurementClick = {},
            onAppointmentsClick = {},
            onProfileClick = {}
        )
    }
}
