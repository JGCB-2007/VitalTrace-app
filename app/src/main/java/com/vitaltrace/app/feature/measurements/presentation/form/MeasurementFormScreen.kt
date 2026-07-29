package com.vitaltrace.app.feature.measurements.presentation.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaltrace.app.feature.measurements.presentation.form.components.BloodPressureFields
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementDateTimeFields
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementFormActions
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementFormHeader
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementInformationCard
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementNoteField
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MeasurementFormScreen(
    onNavigateBack: () -> Unit,
    onMeasurementSaved: () -> Unit,
    viewModel: MeasurementFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                MeasurementFormUiEffect.MeasurementSaved -> onMeasurementSaved()
            }
        }
    }

    MeasurementFormContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onSystolicChange = viewModel::updateSystolic,
        onDiastolicChange = viewModel::updateDiastolic,
        onDateChange = viewModel::updateDate,
        onTimeChange = viewModel::updateTime,
        onNoteChange = viewModel::updateNote,
        onSaveClick = viewModel::saveMeasurement
    )
}

@Composable
private fun MeasurementFormContent(
    uiState: MeasurementFormUiState,
    onNavigateBack: () -> Unit,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    onDateChange: (java.time.LocalDate) -> Unit,
    onTimeChange: (java.time.LocalTime) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(containerColor = VitalTraceWarmBackground) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(
                start = 24.dp,
                top = 28.dp,
                end = 24.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item {
                MeasurementFormHeader(onBackClick = onNavigateBack)
            }
            item {
                BloodPressureFields(
                    systolic = uiState.systolic,
                    diastolic = uiState.diastolic,
                    systolicError = uiState.systolicError,
                    diastolicError = uiState.diastolicError,
                    onSystolicChange = onSystolicChange,
                    onDiastolicChange = onDiastolicChange
                )
            }
            item {
                MeasurementDateTimeFields(
                    date = uiState.date,
                    time = uiState.time,
                    onDateChange = onDateChange,
                    onTimeChange = onTimeChange
                )
            }
            item {
                MeasurementNoteField(
                    note = uiState.note,
                    onNoteChange = onNoteChange
                )
            }
            item {
                MeasurementInformationCard()
            }
            item {
                MeasurementFormActions(
                    isSaving = uiState.isSaving,
                    onSaveClick = onSaveClick,
                    onCancelClick = onNavigateBack
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MeasurementFormScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        MeasurementFormContent(
            uiState = MeasurementFormUiState(),
            onNavigateBack = {},
            onSystolicChange = {},
            onDiastolicChange = {},
            onDateChange = {},
            onTimeChange = {},
            onNoteChange = {},
            onSaveClick = {}
        )
    }
}
