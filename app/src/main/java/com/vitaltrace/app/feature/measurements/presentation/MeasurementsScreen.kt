package com.vitaltrace.app.feature.measurements.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.AdaptivePortalScaffold
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementHistoryCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementFilterBar
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsEmptyState
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsErrorState
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsHeader
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementsLoadingState
import com.vitaltrace.app.feature.measurements.presentation.components.AdvancedMeasurementFilters
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementInsightsCard
import com.vitaltrace.app.feature.measurements.presentation.detail.MeasurementDetailSheet
import com.vitaltrace.app.ui.theme.VitalTraceTheme

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
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onFilterSelected = viewModel::selectFilter,
        onQueryChange = viewModel::updateQuery,
        onTypeSelected = viewModel::selectType,
        onPeriodSelected = viewModel::selectPeriod,
        onAttentionToggle = viewModel::toggleAttentionOnly,
        onClearAdvancedFilters = viewModel::clearAdvancedFilters,
        onMeasurementClick = viewModel::showMeasurementDetail,
        onDismissMeasurementDetail = viewModel::dismissMeasurementDetail,
        onHomeClick = onHomeClick,
        onAddMeasurementClick = onAddMeasurementClick,
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeasurementsContent(
    uiState: MeasurementsUiState,
    onRetryClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onFilterSelected: (MeasurementFilter) -> Unit,
    onQueryChange: (String) -> Unit,
    onTypeSelected: (String?) -> Unit,
    onPeriodSelected: (MeasurementPeriod) -> Unit,
    onAttentionToggle: () -> Unit,
    onClearAdvancedFilters: () -> Unit,
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

    AdaptivePortalScaffold(
        selectedDestination = HomeBottomDestination.MEASUREMENTS,
        onHomeClick = onHomeClick,
        onMeasurementsClick = {},
        onAppointmentsClick = onAppointmentsClick,
        onProfileClick = onProfileClick,
        containerColor = MaterialTheme.colorScheme.background
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
            is MeasurementsContentState.Success -> PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                MeasurementsBody(
                    content = contentState.content,
                    selectedFilter = uiState.selectedFilter,
                    visibleMeasurements = uiState.visibleMeasurements,
                    onFilterSelected = onFilterSelected,
                    query = uiState.query,
                    availableTypes = uiState.availableTypes,
                    selectedType = uiState.selectedTypeName,
                    selectedPeriod = uiState.selectedPeriod,
                    attentionOnly = uiState.attentionOnly,
                    chartMeasurements = uiState.chartMeasurements,
                    onQueryChange = onQueryChange,
                    onTypeSelected = onTypeSelected,
                    onPeriodSelected = onPeriodSelected,
                    onAttentionToggle = onAttentionToggle,
                    onClearAdvancedFilters = onClearAdvancedFilters,
                    onAddMeasurementClick = onAddMeasurementClick,
                    onMeasurementClick = onMeasurementClick,
                    onLoadMore = onLoadMore,
                    isLoadingMore = uiState.isLoadingMore,
                    modifier = Modifier.widthIn(max = 720.dp).align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun MeasurementsBody(
    content: MeasurementsContentUiModel,
    selectedFilter: MeasurementFilter,
    visibleMeasurements: List<MeasurementUiModel>,
    onFilterSelected: (MeasurementFilter) -> Unit,
    query: String,
    availableTypes: List<String>,
    selectedType: String?,
    selectedPeriod: MeasurementPeriod,
    attentionOnly: Boolean,
    chartMeasurements: List<MeasurementUiModel>,
    onQueryChange: (String) -> Unit,
    onTypeSelected: (String?) -> Unit,
    onPeriodSelected: (MeasurementPeriod) -> Unit,
    onAttentionToggle: () -> Unit,
    onClearAdvancedFilters: () -> Unit,
    onAddMeasurementClick: () -> Unit,
    onMeasurementClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp, 30.dp, 24.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { MeasurementsHeader(onAddMeasurementClick = onAddMeasurementClick) }
        item {
            MeasurementInsightsCard(
                measurements = chartMeasurements,
                availableTypes = availableTypes,
                selectedType = selectedType,
                selectedPeriod = selectedPeriod,
                onTypeSelected = onTypeSelected,
                onPeriodSelected = onPeriodSelected
            )
        }
        item {
            AdvancedMeasurementFilters(
                query = query,
                availableTypes = availableTypes,
                selectedType = selectedType,
                attentionOnly = attentionOnly,
                onQueryChange = onQueryChange,
                onTypeSelected = onTypeSelected,
                onAttentionToggle = onAttentionToggle,
                onClear = onClearAdvancedFilters
            )
        }
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
        if (visibleMeasurements.isEmpty()) {
            item {
                if (content.latestMeasurement == null && query.isBlank() && selectedType == null && !attentionOnly) {
                    MeasurementsEmptyState(onAddMeasurementClick = onAddMeasurementClick)
                } else {
                    androidx.compose.material3.Text(
                        text = "No encontramos mediciones con estos filtros.",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp)
                    )
                }
            }
        } else if (visibleMeasurements.isNotEmpty()) {
            item {
                MeasurementHistoryCard(
                    measurements = visibleMeasurements,
                    onMeasurementClick = onMeasurementClick
                )
            }
        }
        if (content.currentPage < content.lastPage) {
            item(key = "load-more-${content.currentPage}") {
                LaunchedEffect(content.currentPage) { onLoadMore() }
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    if (isLoadingMore) CircularProgressIndicator()
                }
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
            onRefresh = {},
            onLoadMore = {},
            onFilterSelected = {},
            onQueryChange = {},
            onTypeSelected = {},
            onPeriodSelected = {},
            onAttentionToggle = {},
            onClearAdvancedFilters = {},
            onMeasurementClick = {},
            onDismissMeasurementDetail = {},
            onHomeClick = {},
            onAddMeasurementClick = {},
            onAppointmentsClick = {},
            onProfileClick = {}
        )
    }
}
