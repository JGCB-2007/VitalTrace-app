package com.vitaltrace.app.feature.treatments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination
import com.vitaltrace.app.feature.home.presentation.components.HomeBottomBar
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentCard
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentsEmptyState
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentsErrorState
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentsLoadingState
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun TreatmentsScreen(
    onTreatmentClick: (Long) -> Unit,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: TreatmentsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = VitalTraceWarmBackground,
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(24.dp, 30.dp, 24.dp, 30.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.treatments_title),
                                color = VitalTraceNavy,
                                fontFamily = FontFamily.Serif,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(contentState.content.treatments, key = TreatmentUiModel::id) { treatment ->
                            TreatmentCard(
                                treatment = treatment,
                                onClick = {
                                    viewModel.selectTreatment(treatment.id)
                                    onTreatmentClick(treatment.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
