package com.vitaltrace.app.feature.relatives.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.vitaltrace.app.feature.relatives.presentation.components.RelativeCard
import com.vitaltrace.app.feature.relatives.presentation.components.RelativeConfirmationDialog
import com.vitaltrace.app.feature.relatives.presentation.components.RelativesEmptyState
import com.vitaltrace.app.feature.relatives.presentation.components.RelativesErrorState
import com.vitaltrace.app.feature.relatives.presentation.components.RelativesLoadingState
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelativesScreen(
    onNavigateBack: () -> Unit,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: RelativesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val message = state.message

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    state.confirmation?.let {
        RelativeConfirmationDialog(
            onConfirm = viewModel::confirmAction,
            onDismiss = viewModel::dismissConfirmation
        )
    }

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            stringResource(R.string.relatives_title),
                            fontFamily = FontFamily.Serif,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.relatives_description),
                            color = androidx.compose.ui.graphics.Color(0xFF5C6870),
                            fontSize = 12.sp,
                            lineHeight = 15.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            stringResource(R.string.relatives_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitalTraceWarmBackground,
                    titleContentColor = VitalTraceNavy,
                    navigationIconContentColor = VitalTraceNavy
                )
            )
        },
        bottomBar = {
            HomeBottomBar(
                selectedDestination = HomeBottomDestination.PROFILE,
                onHomeClick = onHomeClick,
                onMeasurementsClick = onMeasurementsClick,
                onAppointmentsClick = onAppointmentsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        when (val content = state.content) {
            RelativesContentState.Loading -> RelativesLoadingState(Modifier.padding(padding))
            is RelativesContentState.Error -> RelativesErrorState(
                content.message,
                viewModel::retry,
                Modifier.padding(padding).padding(horizontal = 24.dp)
            )
            is RelativesContentState.Success -> {
                if (content.relatives.isEmpty()) {
                    RelativesEmptyState(Modifier.padding(padding).padding(horizontal = 24.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(24.dp, 24.dp, 24.dp, 36.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(content.relatives, key = RelativeUiModel::id) { relative ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically { it / 5 }
                            ) {
                                RelativeCard(
                                    relative = relative,
                                    isActionInProgress = state.actionInProgressId == relative.id,
                                    onAuthorize = {
                                        viewModel.requestAction(relative.id, RelativeAction.AUTHORIZE)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
