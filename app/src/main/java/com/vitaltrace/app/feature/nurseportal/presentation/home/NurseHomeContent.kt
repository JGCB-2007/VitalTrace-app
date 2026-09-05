package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
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
import com.vitaltrace.app.feature.home.presentation.FollowUpStatusUiModel
import com.vitaltrace.app.feature.home.presentation.NextAppointmentUiModel
import com.vitaltrace.app.feature.home.presentation.components.FollowUpStatusCard
import com.vitaltrace.app.feature.home.presentation.components.HomeHeader
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAppointment
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
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
            FollowUpStatusCard(
                status = FollowUpStatusUiModel(
                    status = "${summary?.assignedPatientsCount ?: 0} asignados",
                    title = "Pacientes bajo tu seguimiento",
                    description = "${summary?.alerts?.pending ?: 0} alertas pendientes · ${summary?.alerts?.critical ?: 0} críticas"
                )
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
private fun NurseHomeAccessCard(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(5.dp)) {
        Row(Modifier.padding(22.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFFDDF4F2), shape = RoundedCornerShape(18.dp)) {
                Icon(icon, null, Modifier.padding(14.dp), tint = VitalTraceTeal)
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(description, color = Color(0xFF53636D), modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = VitalTraceTeal)
        }
    }
}

private fun NurseAppointment.toNextAppointmentUi(patientName: String) = NextAppointmentUiModel(
    id = id,
    professionalName = patientName.ifBlank { "Paciente" },
    reason = reason,
    date = scheduledAt.substringBefore(' '),
    time = scheduledAt.substringAfter(' ', ""),
    status = status.lowercase().replaceFirstChar { it.titlecase() }
)

internal fun nurseGreeting(): String {
    val hour = SimpleDateFormat("H", Locale.getDefault()).format(Date()).toIntOrNull() ?: 12
    return when { hour < 12 -> "Buenos días"; hour < 19 -> "Buenas tardes"; else -> "Buenas noches" }
}