package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.core.presentation.components.PortalBottomDestination
import com.vitaltrace.app.core.presentation.components.VitalTracePortalBottomBar
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.home.presentation.components.LoadingState
import com.vitaltrace.app.feature.home.presentation.components.LogoutConfirmationDialog
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursePortalScreen(
    onLogout: () -> Unit,
    onEducationClick: (String, String) -> Unit,
    viewModel: NursePortalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirmLogout by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it is NursePortalEffect.NavigateToLogin) onLogout() }
    }
    if (confirmLogout) LogoutConfirmationDialog(
        onConfirm = { confirmLogout = false; viewModel.logout() },
        onDismiss = { confirmLogout = false }
    )

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        topBar = {
            TopAppBar(
                title = { Text(state.section.title, fontFamily = FontFamily.Serif, fontSize = 25.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (state.section == NurseSection.PATIENTS && state.selectedPatient != null) {
                        IconButton(viewModel::clearPatient) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver a pacientes") }
                    }
                },
                actions = {
                    if (state.section != NurseSection.HOME) IconButton({ confirmLogout = true }) {
                        Icon(Icons.AutoMirrored.Rounded.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitalTraceWarmBackground,
                    titleContentColor = VitalTraceNavy,
                    navigationIconContentColor = VitalTraceNavy,
                    actionIconContentColor = VitalTraceNavy
                )
            )
        },
        bottomBar = {
            VitalTracePortalBottomBar(state.section, nurseDestinations, viewModel::selectSection)
        }
    ) { padding ->
        when {
            state.loading && state.summary == null && state.patients.isEmpty() -> LoadingState(Modifier.fillMaxSize().padding(padding))
            state.error != null && state.summary == null -> HomeErrorState(state.error.orEmpty(), viewModel::retry, Modifier.fillMaxSize().padding(padding))
            else -> when (state.section) {
                NurseSection.HOME -> NurseHomeContent(state, viewModel, { confirmLogout = true }, Modifier.padding(padding))
                NurseSection.PATIENTS -> NursePatientsContent(state, viewModel, onEducationClick, Modifier.padding(padding))
                NurseSection.ALERTS -> NurseAlertsContent(state, viewModel, Modifier.padding(padding))
                NurseSection.APPOINTMENTS -> NurseAppointmentsContent(state, viewModel, Modifier.padding(padding))
                NurseSection.PROFILE -> NurseProfileContent(state, { confirmLogout = true }, Modifier.padding(padding))
            }
        }
    }
}

private val nurseDestinations = listOf(
    PortalBottomDestination(NurseSection.HOME, "Inicio", Icons.Rounded.Home),
    PortalBottomDestination(NurseSection.PATIENTS, "Pacientes", Icons.Rounded.People),
    PortalBottomDestination(NurseSection.ALERTS, "Alertas", Icons.Rounded.Warning),
    PortalBottomDestination(NurseSection.APPOINTMENTS, "Citas", Icons.Rounded.CalendarMonth),
    PortalBottomDestination(NurseSection.PROFILE, "Perfil", Icons.Rounded.Person)
)

internal val NurseSection.title: String get() = when (this) {
    NurseSection.HOME -> "Inicio"
    NurseSection.PATIENTS -> "Pacientes"
    NurseSection.ALERTS -> "Alertas"
    NurseSection.APPOINTMENTS -> "Citas"
    NurseSection.PROFILE -> "Perfil"
}