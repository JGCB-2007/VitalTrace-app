package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.AppShellViewModel
import com.vitaltrace.app.core.presentation.components.PortalBottomDestination
import com.vitaltrace.app.core.presentation.components.ThemePreferenceCard
import com.vitaltrace.app.core.presentation.components.VitalTracePortalBottomBar
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.home.presentation.components.LoadingState
import com.vitaltrace.app.feature.home.presentation.components.LogoutConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursePortalScreen(
    onLogout: () -> Unit,
    onEducationClick: (String, String) -> Unit,
    viewModel: NursePortalViewModel = hiltViewModel(),
    appShellViewModel: AppShellViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appShellState by appShellViewModel.uiState.collectAsStateWithLifecycle()
    var confirmLogout by remember { mutableStateOf(false) }
    var showThemeSettings by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it is NursePortalEffect.NavigateToLogin) onLogout() }
    }
    LaunchedEffect(state.mutationMessage) {
        state.mutationMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessage()
        }
    }
    BackHandler(
        enabled = state.selectedPatient != null || state.section != NurseSection.HOME
    ) {
        when {
            state.selectedPatient != null && state.patientSection != NursePatientSection.SUMMARY ->
                viewModel.selectPatientSection(NursePatientSection.SUMMARY)
            state.selectedPatient != null -> viewModel.clearPatient()
            else -> viewModel.selectSection(NurseSection.HOME)
        }
    }
    if (confirmLogout) LogoutConfirmationDialog(
        onConfirm = { confirmLogout = false; viewModel.logout() },
        onDismiss = { confirmLogout = false }
    )
    if (showThemeSettings) {
        ModalBottomSheet(onDismissRequest = { showThemeSettings = false }) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Apariencia",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Serif,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
                ThemePreferenceCard(
                    theme = appShellState.preferences.theme,
                    onThemeChange = appShellViewModel::setTheme
                )
                Spacer(Modifier.height(18.dp))
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.section.title, fontFamily = FontFamily.Serif, fontSize = 25.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (state.section == NurseSection.PATIENTS && state.selectedPatient != null) {
                        IconButton(viewModel::clearPatient) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver a pacientes") }
                    }
                },
                actions = {
                    IconButton(onClick = { showThemeSettings = true }) {
                        Icon(Icons.Rounded.Palette, "Cambiar tema")
                    }
                    if (state.section != NurseSection.HOME) IconButton({ confirmLogout = true }) {
                        Icon(Icons.AutoMirrored.Rounded.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
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
            else -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                val portalModifier = Modifier.fillMaxSize().widthIn(max = 840.dp)
                AnimatedContent(
                    targetState = state.section,
                    modifier = portalModifier,
                    transitionSpec = {
                        fadeIn(tween(190)) togetherWith fadeOut(tween(120))
                    },
                    label = "nurse-portal-section"
                ) { section ->
                    val sectionModifier = Modifier.fillMaxSize()
                    when (section) {
                        NurseSection.HOME -> NurseHomeContent(state, viewModel, { confirmLogout = true }, sectionModifier)
                        NurseSection.PATIENTS -> NursePatientsContent(state, viewModel, onEducationClick, sectionModifier)
                        NurseSection.ALERTS -> NurseAlertsContent(state, viewModel, sectionModifier)
                        NurseSection.APPOINTMENTS -> NurseAppointmentsContent(state, viewModel, sectionModifier)
                        NurseSection.PROFILE -> NurseProfileContent(
                            state = state,
                            theme = appShellState.preferences.theme,
                            onThemeChange = appShellViewModel::setTheme,
                            onLogout = { confirmLogout = true },
                            modifier = sectionModifier
                        )
                    }
                }
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
