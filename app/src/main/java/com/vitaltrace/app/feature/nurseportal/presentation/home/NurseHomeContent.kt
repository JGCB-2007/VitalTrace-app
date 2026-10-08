package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.home.presentation.NextAppointmentUiModel
import com.vitaltrace.app.feature.home.presentation.components.HomeHeader
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.core.presentation.components.OfflineStatusBanner
import com.vitaltrace.app.core.presentation.formatClinicalDate
import com.vitaltrace.app.core.presentation.formatClinicalTime
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAppointment
import com.vitaltrace.app.feature.nurseportal.domain.model.NursePatient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun NurseHomeContent(state: NursePortalUiState, viewModel: NursePortalViewModel, onLogout: () -> Unit, modifier: Modifier) {
    val summary = state.summary
    val nurse = summary?.nurse
    val next = summary?.appointments?.firstOrNull()
    val patientName = next?.let { appointment -> state.patients.firstOrNull { it.id == appointment.patientId }?.fullName }.orEmpty()

    state.selectedAppointment?.let { detail ->
        AppointmentDetailSheet(detail.toAppointmentDetailUiModel(state.patients.firstOrNull { it.id == detail.patientId }?.fullName), viewModel::dismissAppointment)
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 22.dp, end = 24.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { OfflineStatusBanner() }
        item {
            HomeHeader(
                greeting = nurseGreeting(),
                patientName = nurse?.fullName.orEmpty().ifBlank { "Enfermería" },
                patientInitials = initials(nurse?.fullName.orEmpty()),
                isLoggingOut = false,
                onLogoutClick = onLogout,
                unreadNotificationsCount = 0,
                onNotificationsClick = {},
                subtitle = listOfNotNull("Enfermería", nurse?.professionalCode).joinToString(" · "),
                showNotifications = false
            )
        }
        item {
            NurseDashboardOverviewCard(
                assignedPatients = summary?.assignedPatientsCount ?: 0,
                pendingAlerts = summary?.alerts?.pending ?: 0,
                criticalAlerts = summary?.alerts?.critical ?: 0,
                appointments = summary?.appointments?.size ?: 0
            )
        }
        item {
            NursePriorityQueueCard(
                patients = state.patients,
                onPatientClick = viewModel::selectPatient,
                onViewAllAlerts = { viewModel.selectSection(NurseSection.ALERTS) }
            )
        }
        item {
            NextAppointmentCard(
                appointment = next?.toNextAppointmentUi(patientName),
                onDetailClick = { next?.let { viewModel.loadAppointment(it.id) } },
                showDetailAction = next != null
            )
        }
        item {
            NurseHomeAccessCard(Icons.Rounded.People, "Pacientes asignados", "Consulta el resumen clínico y registra mediciones.") {
                viewModel.selectSection(NurseSection.PATIENTS)
            }
        }
        item {
            NurseHomeAccessCard(Icons.Rounded.Warning, "Alertas pendientes", "Revisa, clasifica o escala alertas clínicas autorizadas.") {
                viewModel.selectSection(NurseSection.ALERTS)
            }
        }
    }
}

@Composable
private fun NursePriorityQueueCard(
    patients: List<NursePatient>,
    onPatientClick: (NursePatient) -> Unit,
    onViewAllAlerts: () -> Unit
) {
    val priorities = remember(patients) {
        patients
            .filter { it.activeAlerts > 0 || it.criticalAlerts > 0 }
            .sortedWith(compareByDescending<NursePatient> { it.criticalAlerts }.thenByDescending { it.activeAlerts })
            .take(3)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("PRIORIDADES DE HOY", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
                    Text("Pacientes que requieren seguimiento", fontFamily = FontFamily.Serif, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                if (priorities.isNotEmpty()) TextButton(onClick = onViewAllAlerts) { Text("Ver alertas", fontWeight = FontWeight.Bold) }
            }
            if (priorities.isEmpty()) {
                Surface(color = Color(0xFFDDF1E7), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF23805F))
                        Text("No hay pacientes con alertas activas.", color = Color(0xFF1D654D), fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                priorities.forEachIndexed { index, patient ->
                    Surface(
                        onClick = { onPatientClick(patient) },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                color = if (patient.criticalAlerts > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    if (patient.criticalAlerts > 0) Icons.Rounded.Error else Icons.Rounded.Warning,
                                    null,
                                    Modifier.padding(10.dp),
                                    tint = if (patient.criticalAlerts > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(patient.fullName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    when {
                                        patient.criticalAlerts > 0 -> "${patient.criticalAlerts} crítica(s) · ${patient.activeAlerts} activa(s)"
                                        else -> "${patient.activeAlerts} alerta(s) activa(s)"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Rounded.ChevronRight, "Abrir paciente", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                    if (index < priorities.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun NurseHomeAccessCard(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(Modifier.padding(22.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(18.dp)) {
                Icon(icon, null, Modifier.padding(14.dp), tint = MaterialTheme.colorScheme.secondary)
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

private data class NurseDashboardMetric(val label: String, val value: Int, val icon: ImageVector)

@Composable
private fun NurseDashboardOverviewCard(
    assignedPatients: Int,
    pendingAlerts: Int,
    criticalAlerts: Int,
    appointments: Int
) {
    val requiresAttention = criticalAlerts > 0
    val accent = if (requiresAttention) MaterialTheme.colorScheme.error else Color(0xFF23805F)
    val metrics = listOf(
        NurseDashboardMetric("Pacientes", assignedPatients, Icons.Rounded.People),
        NurseDashboardMetric("Pendientes", pendingAlerts, Icons.Rounded.Warning),
        NurseDashboardMetric("Críticas", criticalAlerts, Icons.Rounded.Error),
        NurseDashboardMetric("Citas", appointments, Icons.Rounded.CalendarMonth)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(color = accent.copy(alpha = 0.14f), shape = RoundedCornerShape(18.dp)) {
                    Icon(
                        if (requiresAttention) Icons.Rounded.Error else Icons.Rounded.CheckCircle,
                        null,
                        Modifier.padding(13.dp).size(30.dp),
                        tint = accent
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        if (requiresAttention) "Prioridad clínica" else "Resumen de seguimiento",
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (requiresAttention) "$criticalAlerts alerta(s) requieren atención prioritaria."
                        else "No hay alertas críticas pendientes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            metrics.chunked(2).forEach { rowMetrics ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowMetrics.forEach { metric ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Icon(metric.icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
                                    Text(metric.value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                }
                                Text(metric.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun NurseAppointment.toNextAppointmentUi(patientName: String) = NextAppointmentUiModel(
    id = id,
    professionalName = patientName.ifBlank { "Paciente" },
    reason = reason,
    date = formatClinicalDate(scheduledAt),
    time = formatClinicalTime(scheduledAt),
    status = status.lowercase().replaceFirstChar { it.titlecase() }
)

internal fun nurseGreeting(): String {
    val hour = SimpleDateFormat("H", Locale.getDefault()).format(Date()).toIntOrNull() ?: 12
    return when { hour < 12 -> "Buenos días"; hour < 19 -> "Buenas tardes"; else -> "Buenas noches" }
}
