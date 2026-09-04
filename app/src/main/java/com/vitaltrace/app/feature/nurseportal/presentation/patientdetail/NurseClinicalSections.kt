package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.feature.clinicalhistory.presentation.ClinicalHistoryContent
import com.vitaltrace.app.feature.home.presentation.components.RegisterMeasurementButton
import com.vitaltrace.app.feature.measurements.presentation.MeasurementsMapper
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementHistoryCard
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseMeasurement
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseTreatment
import com.vitaltrace.app.feature.treatments.presentation.TreatmentsMapper
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentCard

@Composable
internal fun NurseMeasurementsContent(state: NursePortalUiState, viewModel: NursePortalViewModel, modifier: Modifier) {
    val content = remember(state.measurements) { MeasurementsMapper().map(state.measurements.map(NurseMeasurement::toPatientMeasurement)) }
    var showForm by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item { RegisterMeasurementButton(onClick = { showForm = true }) }
        content.latestMeasurement?.let { item { LatestMeasurementCard(it, onClick = {}) } }
        if (content.measurements.isNotEmpty()) {
            item { NurseSectionHeader(Icons.Rounded.MonitorHeart, "Historial de mediciones") }
            item { MeasurementHistoryCard(content.measurements, onMeasurementClick = {}) }
        } else if (content.latestMeasurement == null) {
            item { NurseInlineEmpty(Icons.Rounded.MonitorHeart, "Aún no hay mediciones disponibles.") }
        }
    }
    if (showForm) NurseMeasurementFormSheet(
        state = state,
        onDismiss = { showForm = false },
        onSubmit = { typeId, value, unit, date, observation ->
            viewModel.createMeasurement(typeId, value, unit, date, observation)
            showForm = false
        }
    )
}

@Composable
internal fun NurseTreatmentsContent(treatments: List<NurseTreatment>, modifier: Modifier) {
    val content = remember(treatments) { TreatmentsMapper().map(treatments.map(NurseTreatment::toPatientTreatment)).treatments }
    if (content.isEmpty()) {
        NurseCenteredEmpty(Icons.Rounded.Medication, "Sin tratamientos", "No hay tratamientos activos.", modifier)
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(content, key = { "treatment-${it.id}" }) { TreatmentCard(it, onClick = {}) }
    }
}

@Composable
internal fun NurseHistoryContent(state: NursePortalUiState, onEducationClick: (String, String) -> Unit, modifier: Modifier) {
    val history = state.history
    if (history == null) {
        NurseCenteredEmpty(Icons.Rounded.HistoryEdu, "Sin historial", "Aún no hay información clínica registrada.", modifier)
        return
    }
    ClinicalHistoryContent(
        history = history.toPatientClinicalHistory(),
        patientName = state.selectedPatient?.fullName.orEmpty(),
        onEducationClick = onEducationClick,
        modifier = modifier,
        showEducationActions = true
    )
}