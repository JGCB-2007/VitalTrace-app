package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.core.presentation.components.ObservedPatientHeader
import com.vitaltrace.app.core.presentation.localization.EnumDisplayEs
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.home.presentation.FollowUpStatusUiModel
import com.vitaltrace.app.feature.home.presentation.components.FollowUpStatusCard
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.feature.measurements.presentation.MeasurementsMapper
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.nurseportal.domain.model.*
import com.vitaltrace.app.feature.treatments.presentation.TreatmentsMapper
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentCard
import com.vitaltrace.app.ui.theme.*

@Composable
internal fun NursePatientsContent(state: NursePortalUiState, viewModel: NursePortalViewModel, onEducationClick: (String, String) -> Unit, modifier: Modifier) {
    if (state.selectedPatient == null) NursePatientSelector(state, viewModel, modifier)
    else NursePatientWorkspace(state, viewModel, onEducationClick, modifier)
}

@Composable
private fun NursePatientSelector(state: NursePortalUiState, viewModel: NursePortalViewModel, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = state.search,
                onValueChange = viewModel::search,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar paciente") },
                placeholder = { Text("Nombre o expediente") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = VitalTraceTeal,
                    unfocusedBorderColor = Color(0xFFE1DDD3)
                )
            )
        }
        if (state.patients.isEmpty()) item { NurseInlineEmpty(Icons.Rounded.People, "No tienes pacientes asignados.") }
        else items(state.patients, key = NursePatient::id) { NursePatientCard(it, viewModel::selectPatient) }
    }
}

@Composable
private fun NursePatientWorkspace(state: NursePortalUiState, viewModel: NursePortalViewModel, onEducationClick: (String, String) -> Unit, modifier: Modifier) {
    val patient = requireNotNull(state.selectedPatient)
    val sections = remember { listOf(NursePatientSection.SUMMARY, NursePatientSection.MEASUREMENTS, NursePatientSection.TREATMENTS, NursePatientSection.HISTORY, NursePatientSection.ALERTS) }
    Column(modifier.fillMaxSize()) {
        ObservedPatientHeader(
            greeting = nurseGreeting(),
            authenticatedName = state.summary?.nurse?.fullName.orEmpty(),
            patientName = patient.fullName,
            patientRecordNumber = patient.recordNumber,
            canChange = state.patients.size > 1,
            onChange = viewModel::clearPatient,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
        SecondaryScrollableTabRow(
            selectedTabIndex = sections.indexOf(state.patientSection).coerceAtLeast(0),
            containerColor = VitalTraceWarmBackground,
            contentColor = VitalTraceNavy,
            edgePadding = 20.dp,
            divider = {}
        ) {
            sections.forEach { section ->
                Tab(
                    selected = state.patientSection == section,
                    onClick = { viewModel.selectPatientSection(section) },
                    text = { Text(section.label, maxLines = 1, fontWeight = FontWeight.SemiBold) }
                )
            }
        }
        when (state.patientSection) {
            NursePatientSection.SUMMARY, NursePatientSection.DIAGNOSES -> NursePatientSummaryContent(state, viewModel, onEducationClick, Modifier.weight(1f))
            NursePatientSection.MEASUREMENTS -> NurseMeasurementsContent(state, viewModel, Modifier.weight(1f))
            NursePatientSection.TREATMENTS -> NurseTreatmentsContent(state.treatments, Modifier.weight(1f))
            NursePatientSection.HISTORY -> NurseHistoryContent(state, onEducationClick, Modifier.weight(1f))
            NursePatientSection.ALERTS -> NursePatientAlertsContent(state, viewModel, Modifier.weight(1f))
        }
    }
}

@Composable
private fun NursePatientSummaryContent(state: NursePortalUiState, viewModel: NursePortalViewModel, onEducationClick: (String, String) -> Unit, modifier: Modifier) {
    val summary = state.patientSummary
    val measurements = remember(summary?.recentMeasurements) { MeasurementsMapper().map(summary?.recentMeasurements.orEmpty().map(NurseMeasurement::toPatientMeasurement)) }
    val treatments = remember(summary?.activeTreatments) { TreatmentsMapper().map(summary?.activeTreatments.orEmpty().map(NurseTreatment::toPatientTreatment)).treatments }
    LazyColumn(modifier, contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { FollowUpStatusCard(FollowUpStatusUiModel(state.profile?.status ?: state.selectedPatient?.status.orEmpty(), "Estado del paciente", "Información clínica disponible en modo consulta.")) }
        item {
            NextAppointmentCard(
                appointment = summary?.upcomingAppointment?.toAppointmentUiModel(state.selectedPatient?.fullName.orEmpty())?.let { com.vitaltrace.app.feature.home.presentation.NextAppointmentUiModel(it.id, it.professionalName, it.reason, it.date, it.time, it.status.name.lowercase().replaceFirstChar(Char::titlecase)) },
                onDetailClick = { summary?.upcomingAppointment?.let { viewModel.loadAppointment(it.id) } },
                showDetailAction = summary?.upcomingAppointment != null
            )
        }
        measurements.latestMeasurement?.let { item { LatestMeasurementCard(it, onClick = { viewModel.selectPatientSection(NursePatientSection.MEASUREMENTS) }) } }
        if (treatments.isNotEmpty()) {
            item { NurseSectionHeader(Icons.Rounded.Medication, "Tratamientos activos") }
            items(treatments.take(3), key = { "summary-treatment-${it.id}" }) { TreatmentCard(it, onClick = { viewModel.selectPatientSection(NursePatientSection.TREATMENTS) }) }
        }
        if (summary?.diagnoses?.isNotEmpty() == true) {
            item { NurseSectionHeader(Icons.Rounded.HealthAndSafety, "Diagnósticos") }
            items(summary.diagnoses, key = { "summary-diagnosis-${it.id}" }) { NurseDiagnosisCard(it, onEducationClick) }
        }
        if (summary?.activeAlerts?.isNotEmpty() == true) {
            item { NurseSectionHeader(Icons.Rounded.Warning, "Alertas activas") }
            items(summary.activeAlerts, key = { "summary-alert-${it.id}" }) { NurseAlertCard(it, state.selectedPatient?.fullName) { viewModel.loadAlert(it.id) } }
        }
    }
    state.selectedAppointment?.let { AppointmentDetailSheet(it.toAppointmentDetailUiModel(state.selectedPatient?.fullName), viewModel::dismissAppointment) }
    state.selectedAlert?.let { NurseAlertDetailSheet(it, state, viewModel) }
}

@Composable
private fun NursePatientCard(patient: NursePatient, onClick: (NursePatient) -> Unit) {
    Card(onClick = { onClick(patient) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(50.dp), shape = CircleShape, color = Color(0xFFD9F2F0)) { Icon(Icons.Rounded.Person, null, Modifier.padding(12.dp), tint = VitalTraceTeal) }
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(patient.fullName, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("${patient.recordNumber} · ${patient.age?.let { "$it años" } ?: "Edad no disponible"}", color = Color(0xFF53636D))
                }
                if (patient.criticalAlerts > 0) NurseStatusChip("Crítica", Color(0xFFF3E4E1), Color(0xFF8C3D32))
            }
            HorizontalDivider(color = Color(0xFFE5E0D7))
            NurseDetailRow("Última medición", patient.lastMeasurement?.let { "${it.value} ${it.unit}" } ?: "Sin mediciones")
            NurseDetailRow("Próxima cita", patient.nextAppointment?.scheduledAt ?: "Sin cita próxima")
            if (patient.activeAlerts > 0) Text("${patient.activeAlerts} ${if (patient.activeAlerts == 1) "alerta activa" else "alertas activas"}", color = VitalTraceTeal, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NurseDiagnosisCard(item: NurseDiagnosis, onEducationClick: (String, String) -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(5.dp)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(item.description, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { item.cieCode?.let { NurseStatusChip(it, Color(0xFFDDF4F2), VitalTraceTeal) }; NurseStatusChip(EnumDisplayEs.clinicalStatus(item.status), Color(0xFFDDF1E7), Color(0xFF23805F)) }
            item.cieCode?.let { code -> TextButton({ onEducationClick(code, item.description) }) { Text("Ver información educativa", color = VitalTraceTeal, fontWeight = FontWeight.Bold) } }
        }
    }
}