package com.vitaltrace.app.feature.measurements.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementFilterBar
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementHistoryCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsEmptyState
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsErrorState
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsHeader
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsLoadingState
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun MeasurementsScreen(
    onHomeClick: () -> Unit,
    onAddMeasurementClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: MeasurementsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MeasurementsContent(
        uiState = uiState,
        onFilterSelected = viewModel::selectFilter,
        onRetryClick = viewModel::retry,
        onHomeClick = onHomeClick,
        onAddMeasurementClick = onAddMeasurementClick,
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick
    )
}

@Composable
private fun MeasurementsContent(
    uiState: MeasurementsUiState,
    onFilterSelected: (MeasurementFilter) -> Unit,
    onRetryClick: () -> Unit,
    onHomeClick: () -> Unit,
    onAddMeasurementClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
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
            if (!uiState.isLoading && uiState.errorMessage == null) {
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
        when {
            uiState.isLoading -> MeasurementsLoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            uiState.errorMessage != null -> MeasurementsErrorState(
                message = uiState.errorMessage,
                onRetryClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            else -> MeasurementsBody(
                uiState = uiState,
                onFilterSelected = onFilterSelected,
                onAddMeasurementClick = onAddMeasurementClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun MeasurementsBody(
    uiState: MeasurementsUiState,
    onFilterSelected: (MeasurementFilter) -> Unit,
    onAddMeasurementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 24.dp,
            top = 30.dp,
            end = 24.dp,
            bottom = 88.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            MeasurementsHeader(onAddMeasurementClick = onAddMeasurementClick)
        }
        item {
            MeasurementFilterBar(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }
        uiState.latestMeasurement?.let { latestMeasurement ->
            item {
                LatestMeasurementCard(measurement = latestMeasurement)
            }
        }
        if (uiState.filteredMeasurements.isEmpty()) {
            item {
                MeasurementsEmptyState(onAddMeasurementClick = onAddMeasurementClick)
            }
        } else {
            item {
                MeasurementHistoryCard(measurements = uiState.filteredMeasurements)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MeasurementsScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        MeasurementsContent(
            uiState = previewMeasurementsState(),
            onFilterSelected = {},
            onRetryClick = {},
            onHomeClick = {},
            onAddMeasurementClick = {},
            onAppointmentsClick = {},
            onProfileClick = {}
        )
    }
}

private fun previewMeasurementsState(): MeasurementsUiState {
    return MeasurementsUiState(
        latestMeasurement = MeasurementUiModel(
            id = "latest",
            value = "145/92",
            date = "14 jul",
            time = "9:42 a. m.",
            status = MeasurementStatus.IN_REVIEW
        ),
        measurements = listOf(
            MeasurementUiModel(
                id = "preview",
                value = "138/88",
                date = "13 jul",
                time = "8:15 a. m.",
                status = MeasurementStatus.REVIEWED
            )
        )
    )
}
