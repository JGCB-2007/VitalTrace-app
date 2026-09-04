package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.appointments.presentation.AppointmentUiModel
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentSection
import com.vitaltrace.app.feature.appointments.presentation.components.FeaturedAppointmentCard
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseInfo
import com.vitaltrace.app.feature.profile.presentation.ProfileUserUiModel
import com.vitaltrace.app.feature.profile.presentation.components.ProfileIdentityCard
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
internal fun NurseAppointmentsContent(state: NursePortalUiState, viewModel: NursePortalViewModel, modifier: Modifier) {
    val appointments = remember(state.appointments, state.patients) {
        state.appointments.map { appointment ->
            appointment.toAppointmentUiModel(state.patients.firstOrNull { it.id == appointment.patientId }?.fullName ?: "Paciente")
        }
    }
    val upcoming = appointments.filter { it.status.isUpcoming }
    val previous = appointments.filterNot { it.status.isUpcoming }
    val next = upcoming.minByOrNull(AppointmentUiModel::scheduledAt)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        next?.let { item { FeaturedAppointmentCard(it, onClick = { viewModel.loadAppointment(it.id) }) } }
        item { AppointmentSection("Próximas citas", upcoming.filterNot { it.id == next?.id }, "No hay citas próximas.", viewModel::loadAppointment) }
        item { AppointmentSection("Citas anteriores", previous, "No tienes citas registradas.", viewModel::loadAppointment) }
    }
    state.selectedAppointment?.let { AppointmentDetailSheet(it.toAppointmentDetailUiModel(state.patients.firstOrNull { patient -> patient.id == it.patientId }?.fullName), viewModel::dismissAppointment) }
}

@Composable
internal fun NurseProfileContent(state: NursePortalUiState, onLogout: () -> Unit, modifier: Modifier) {
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
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, Color(0xFFE1DDD3)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = VitalTraceNavy)
            ) {
                Text("Cerrar sesión", fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NurseProfessionalInformationCard(nurse: NurseInfo?) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(5.dp)) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            ProfessionalRow("Código profesional", nurse?.professionalCode.orEmpty())
            HorizontalDivider(color = Color(0xFFE5E0D7))
            ProfessionalRow("Especialidad", nurse?.specialty ?: "No registrada")
        }
    }
}

@Composable
private fun ProfessionalRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 17.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF53636D), fontSize = 17.sp)
        Text(value, Modifier.weight(1f).padding(start = 18.dp), color = VitalTraceNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}