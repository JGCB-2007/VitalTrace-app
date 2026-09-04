package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.core.presentation.components.ObservedPatientHeader
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementNoteField
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAlert
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAlertHistory
import com.vitaltrace.app.ui.theme.*

@Composable
internal fun NurseAlertsContent(state: NursePortalUiState, viewModel: NursePortalViewModel, modifier: Modifier) {
    if (state.alerts.isEmpty()) {
        NurseCenteredEmpty(Icons.Rounded.Warning, "Sin alertas", "No hay alertas pendientes.", modifier)
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(state.alerts, key = NurseAlert::id) { alert ->
            NurseAlertCard(alert, state.patients.firstOrNull { it.id == alert.patientId }?.fullName) { viewModel.loadAlert(alert.id) }
        }
    }
    state.selectedAlert?.let { NurseAlertDetailSheet(it, state, viewModel) }
}

@Composable
internal fun NursePatientAlertsContent(state: NursePortalUiState, viewModel: NursePortalViewModel, modifier: Modifier) {
    if (state.alerts.isEmpty()) {
        NurseCenteredEmpty(Icons.Rounded.Warning, "Sin alertas", "Este paciente no tiene alertas pendientes.", modifier)
        return
    }
    LazyColumn(modifier, contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(state.alerts, key = NurseAlert::id) { alert -> NurseAlertCard(alert, state.selectedPatient?.fullName) { viewModel.loadAlert(alert.id) } }
    }
    state.selectedAlert?.let { NurseAlertDetailSheet(it, state, viewModel) }
}

@Composable
internal fun NurseAlertCard(alert: NurseAlert, patientName: String? = null, onClick: () -> Unit) {
    val colors = severityColors(alert.severity)
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(5.dp)) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = colors.first, shape = RoundedCornerShape(18.dp)) { Icon(Icons.Rounded.Warning, null, Modifier.padding(14.dp), tint = colors.second) }
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    patientName?.let { Text(it, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    Text(alert.description, color = Color(0xFF53636D), maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NurseStatusChip(alert.severity, colors.first, colors.second)
                    NurseStatusChip(alert.status, Color(0xFFDDF4F2), VitalTraceTeal)
                }
                Text(alert.generatedAt, color = Color(0xFF53636D), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NurseAlertDetailSheet(alert: NurseAlert, state: NursePortalUiState, viewModel: NursePortalViewModel) {
    var action by remember { mutableStateOf<String?>(null) }
    ModalBottomSheet(
        onDismissRequest = viewModel::dismissAlert,
        containerColor = VitalTraceWarmBackground,
        scrimColor = Color(0x990C1C29),
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
    ) {
        LazyColumn(
            Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(start = 28.dp, end = 28.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item { Text("Detalle de alerta", color = VitalTraceTeal, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.7.sp) }
            item {
                val patient = state.patients.firstOrNull { it.id == alert.patientId } ?: state.selectedPatient
                ObservedPatientHeader(nurseGreeting(), state.summary?.nurse?.fullName.orEmpty(), patient?.fullName.orEmpty(), patient?.recordNumber, false, {})
            }
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val colors = severityColors(alert.severity)
                            NurseStatusChip(alert.severity, colors.first, colors.second)
                            NurseStatusChip(alert.status, Color(0xFFDDF4F2), VitalTraceTeal)
                        }
                        Text(alert.description, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        NurseDetailRow("Tipo", alert.type)
                        NurseDetailRow("Fecha", alert.generatedAt)
                        alert.measurementId?.let { NurseDetailRow("Medición asociada", "#$it") }
                    }
                }
            }
            if (alert.history.isNotEmpty()) {
                item { NurseSectionHeader(Icons.Rounded.History, "Historial de alerta") }
                items(alert.history, key = NurseAlertHistory::id) { AlertHistoryCard(it) }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Button(
                        onClick = { action = "CLASSIFY" },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)
                    ) { Text("Clasificar", fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    OutlinedButton(
                        onClick = { action = "ESCALATE" },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFFE1DDD3)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VitalTraceNavy)
                    ) { Text("Escalar", fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
    action?.let { selected ->
        AlertActionDialog(
            title = if (selected == "CLASSIFY") "Clasificar alerta" else "Escalar alerta",
            message = if (selected == "CLASSIFY") "Agrega un comentario clínico opcional." else "Confirma que deseas escalar esta alerta.",
            confirmLabel = if (selected == "CLASSIFY") "Clasificar" else "Escalar",
            onDismiss = { action = null },
            onConfirm = { comment ->
                if (selected == "CLASSIFY") viewModel.classify(alert.id, comment) else viewModel.escalate(alert.id, comment)
                action = null
            }
        )
    }
}

@Composable
private fun AlertActionDialog(title: String, message: String, confirmLabel: String, onDismiss: () -> Unit, onConfirm: (String?) -> Unit) {
    var comment by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.Warning, null, tint = VitalTraceTeal) },
        title = { Text(title, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(14.dp)) { Text(message, color = Color(0xFF53636D)); MeasurementNoteField(comment, { comment = it }) } },
        confirmButton = { Button({ onConfirm(comment.takeIf(String::isNotBlank)) }, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)) { Text(confirmLabel) } },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar", color = VitalTraceTeal, fontWeight = FontWeight.Bold) } }
    )
}

@Composable
private fun AlertHistoryCard(item: NurseAlertHistory) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.action, color = VitalTraceNavy, fontWeight = FontWeight.Bold)
            Text(listOfNotNull(item.previousStatus, item.newStatus).joinToString(" → "), color = VitalTraceTeal)
            item.comment?.let { Text(it, color = Color(0xFF53636D)) }
            item.createdAt?.let { Text(it, color = Color(0xFF53636D), style = MaterialTheme.typography.bodySmall) }
        }
    }
}

private fun severityColors(value: String): Pair<Color, Color> = when (value.uppercase()) {
    "CRITICAL" -> Color(0xFFF3E4E1) to Color(0xFF8C3D32)
    "HIGH" -> Color(0xFFFFE8D4) to Color(0xFF9A4F11)
    "MODERATE" -> Color(0xFFFFF2CC) to Color(0xFF796000)
    else -> Color(0xFFDDF4F2) to VitalTraceTeal
}