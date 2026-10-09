package com.vitaltrace.app.feature.treatments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.FilterChip
import androidx.compose.material.icons.rounded.Search
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.ui.theme.SoraFontFamily
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentCard
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentsEmptyState
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentsErrorState
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentsLoadingState
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentsScreen(
    onNavigateBack: () -> Unit,
    onTreatmentClick: (Long) -> Unit,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: TreatmentsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.treatments_title),
                        fontFamily = SoraFontFamily,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.treatment_detail_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            HomeBottomBar(
                selectedDestination = HomeBottomDestination.HOME,
                onHomeClick = onHomeClick,
                onMeasurementsClick = onMeasurementsClick,
                onAppointmentsClick = onAppointmentsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        when (val contentState = state.contentState) {
            TreatmentsContentState.Loading -> TreatmentsLoadingState(Modifier.padding(padding))
            is TreatmentsContentState.Error -> TreatmentsErrorState(
                contentState.message,
                viewModel::retry,
                Modifier.padding(padding)
            )
            is TreatmentsContentState.Success -> {
                if (contentState.content.treatments.isEmpty()) {
                    TreatmentsEmptyState(Modifier.padding(padding))
                } else {
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = viewModel::refresh,
                        modifier = Modifier.fillMaxSize().padding(padding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().widthIn(max = 720.dp).align(Alignment.TopCenter),
                            contentPadding = PaddingValues(24.dp, 30.dp, 24.dp, 30.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            item {
                                OutlinedTextField(
                                    value = state.query,
                                    onValueChange = viewModel::updateQuery,
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                                    placeholder = { Text("Buscar medicamento, indicación o profesional") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(18.dp)
                                )
                            }
                            item {
                                FilterChip(
                                    selected = state.activeOnly,
                                    onClick = viewModel::toggleActiveOnly,
                                    label = { Text("Solo tratamientos activos") }
                                )
                            }
                            if (state.visibleTreatments.isEmpty()) {
                                item {
                                    Text(
                                        "No encontramos tratamientos con estos filtros.",
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 28.dp)
                                    )
                                }
                            }
                            items(state.visibleTreatments, key = TreatmentUiModel::id) { treatment ->
                                TreatmentCard(
                                    treatment = treatment,
                                    onClick = {
                                        viewModel.selectTreatment(treatment.id)
                                        onTreatmentClick(treatment.id)
                                    }
                                )
                            }
                            if (contentState.content.currentPage < contentState.content.lastPage) {
                                item(key = "load-more-${contentState.content.currentPage}") {
                                    LaunchedEffect(contentState.content.currentPage) { viewModel.loadMore() }
                                    Box(
                                        Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (state.isLoadingMore) CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
