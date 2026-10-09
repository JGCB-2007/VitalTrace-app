package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.ui.theme.SoraFontFamily
import com.vitaltrace.app.core.presentation.components.ThemePreferenceCard
import com.vitaltrace.app.core.settings.ThemePreference
import com.vitaltrace.app.feature.appointments.presentation.AppointmentUiModel
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentSection
import com.vitaltrace.app.feature.appointments.presentation.components.FeaturedAppointmentCard
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseInfo
import com.vitaltrace.app.feature.profile.presentation.ProfileUserUiModel
import com.vitaltrace.app.feature.profile.presentation.components.ProfileIdentityCard

@Composable
internal fun NurseAppointmentsContent(state: NursePortalUiState, viewModel: NursePortalViewModel, modifier: Modifier) {
    if (state.appointmentsError != null && state.nurseAppointments.isEmpty()) {
        HomeErrorState(state.appointmentsError, viewModel::retryAppointments, modifier.fillMaxSize())
        return
    }
    val appointments = remember(state.nurseAppointments, state.patients) {
        state.nurseAppointments.map { appointment ->
            appointment.toAppointmentUiModel(state.patients.firstOrNull { it.id == appointment.patientId }?.fullName ?: "Paciente")
        }
    }
    var query by rememberSaveable { mutableStateOf("") }
    val filteredAppointments = remember(appointments, query) {
        if (query.isBlank()) appointments else appointments.filter { appointment ->
            listOf(
                appointment.professionalName,
                appointment.reason,
                appointment.date,
                appointment.status.name
            ).any { it.contains(query.trim(), ignoreCase = true) }
        }
    }
    val upcoming = filteredAppointments.filter { it.status.isUpcoming }
    val previous = filteredAppointments.filterNot { it.status.isUpcoming }
    val next = upcoming.minByOrNull(AppointmentUiModel::scheduledAt)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                placeholder = { Text("Buscar por paciente, motivo o fecha") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp)
            )
        }
        next?.let { item { FeaturedAppointmentCard(it, onClick = { viewModel.loadAppointment(it.id) }) } }
        item { AppointmentSection("Próximas citas", upcoming.filterNot { it.id == next?.id }, "No hay citas próximas.", viewModel::loadAppointment) }
        item { AppointmentSection("Citas anteriores", previous, "No tienes citas registradas.", viewModel::loadAppointment) }
    }
    state.selectedAppointment?.let { AppointmentDetailSheet(it.toAppointmentDetailUiModel(state.patients.firstOrNull { patient -> patient.id == it.patientId }?.fullName), viewModel::dismissAppointment) }
}

@Composable
internal fun NurseProfileContent(
    state: NursePortalUiState,
    theme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier
) {
    val nurse = state.summary?.nurse
    val user = ProfileUserUiModel(
        fullName = nurse?.fullName.orEmpty(),
        initials = initials(nurse?.fullName.orEmpty()),
        identifier = nurse?.professionalCode.orEmpty(),
        email = "",
        phone = null,
        dateOfBirth = null,
        age = null,
        gender = null,
        address = null,
        identificationNumber = null,
        emergencyContactName = null,
        emergencyContactPhone = null,
        accountStatus = "ACTIVE",
        administrativeStatus = "ACTIVE"
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { ProfileIdentityCard(user, roleLabel = "Enfermería") }
        item { NurseProfessionalInformationCard(nurse) }
        item { ThemePreferenceCard(theme = theme, onThemeChange = onThemeChange) }
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Cerrar sesión", fontFamily = SoraFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NurseProfessionalInformationCard(nurse: NurseInfo?) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(5.dp)) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            ProfessionalRow("Código profesional", nurse?.professionalCode.orEmpty())
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfessionalRow("Especialidad", nurse?.specialty ?: "No registrada")
        }
    }
}

@Composable
private fun ProfessionalRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 17.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 17.sp)
        Text(value, Modifier.weight(1f).padding(start = 18.dp), color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}
