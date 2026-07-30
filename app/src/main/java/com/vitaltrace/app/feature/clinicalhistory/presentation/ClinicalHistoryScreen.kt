package com.vitaltrace.app.feature.clinicalhistory.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory
import com.vitaltrace.app.feature.patient.domain.model.ClinicalProfessional
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalHistoryScreen(onNavigateBack: () -> Unit, viewModel: ClinicalHistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ClinicalHistoryDesign(state, onNavigateBack, viewModel::retry)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegacyClinicalHistoryScreen(onNavigateBack: () -> Unit, viewModel: ClinicalHistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(containerColor = VitalTraceWarmBackground, topBar = {
        TopAppBar(title = { Text("Historial clínico") }, navigationIcon = {
            IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
        })
    }) { padding ->
        when (val current = state) {
            ClinicalHistoryUiState.Loading -> Centered(padding) { CircularProgressIndicator() }
            is ClinicalHistoryUiState.Error -> Centered(padding) {
                Text(current.message)
                Button(onClick = viewModel::retry) { Text("Intentar de nuevo") }
            }
            is ClinicalHistoryUiState.Empty -> Centered(padding) {
                Text("Aún no hay información clínica registrada.")
                Text("Expediente ${current.recordNumber}")
            }
            is ClinicalHistoryUiState.Success -> HistoryContent(current.history, padding)
        }
    }
}

@Composable
private fun Centered(padding: PaddingValues, content: @Composable ColumnScope.() -> Unit) = Column(
    Modifier.fillMaxSize().padding(padding).padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
    content = content
)

@Composable
private fun HistoryContent(history: ClinicalHistory, padding: PaddingValues) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Expediente ${history.recordNumber}", style = MaterialTheme.typography.titleMedium) }
        if (history.diagnoses.isNotEmpty()) {
            item { SectionTitle("Diagnósticos") }
            items(history.diagnoses, key = { "diagnosis-${it.id}" }) {
                HistoryCard(it.description, listOfNotNull(it.cieCode, it.diagnosisDate, statusLabel(it.status), professionalName(it.professional)))
            }
        }
        if (history.clinicalEvolutions.isNotEmpty()) {
            item { SectionTitle("Evoluciones clínicas") }
            items(history.clinicalEvolutions, key = { "evolution-${it.id}" }) {
                HistoryCard(it.clinicalSummary, listOfNotNull(it.recordedAt, statusLabel(it.status), professionalName(it.professional)))
            }
        }
        if (history.currentTreatments.isNotEmpty()) {
            item { SectionTitle("Tratamientos actuales") }
            items(history.currentTreatments, key = { "treatment-${it.id}" }) {
                HistoryCard(it.indications, listOfNotNull(it.startDate, it.endDate, professionalName(it.professional)))
            }
        }
        if (history.recentMeasurements.isNotEmpty()) {
            item { SectionTitle("Mediciones recientes") }
            items(history.recentMeasurements, key = { "measurement-${it.id}" }) {
                HistoryCard(it.measurementType?.name ?: "Medición", listOfNotNull("${it.value} ${it.unit}", it.measuredAt, it.observation))
            }
        }
    }
}

@Composable private fun SectionTitle(text: String) = Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

@Composable
private fun HistoryCard(title: String, details: List<String>) = Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        details.filter(String::isNotBlank).forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
}

private fun professionalName(value: ClinicalProfessional?) = value?.fullName?.takeIf(String::isNotBlank)
private fun statusLabel(value: String) = when (value) {
    "ACTIVE" -> "Activo"; "RESOLVED" -> "Resuelto"; "UNDER_REVIEW" -> "En revisión"
    "STABLE" -> "Estable"; "OBSERVATION" -> "En observación"; "DELICATE" -> "Delicado"
    "CRITICAL" -> "Crítico"; "RECOVERY" -> "En recuperación"; else -> value
}
