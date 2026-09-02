package com.vitaltrace.app.feature.relativeportal.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FolderShared
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.appointments.presentation.AppointmentsMapper
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentSection
import com.vitaltrace.app.feature.appointments.presentation.components.FeaturedAppointmentCard
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.clinicalhistory.presentation.ClinicalHistoryContent
import com.vitaltrace.app.feature.home.presentation.HomeSummaryMapper
import com.vitaltrace.app.feature.home.presentation.components.FollowUpStatusCard
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.home.presentation.components.LoadingState
import com.vitaltrace.app.feature.home.presentation.components.LogoutConfirmationDialog
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.feature.home.presentation.components.RecentPressureCard
import com.vitaltrace.app.feature.measurements.presentation.MeasurementsMapper
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementHistoryCard
import com.vitaltrace.app.feature.relativeportal.domain.model.LinkedPatient
import com.vitaltrace.app.feature.relativeportal.presentation.components.RelativePortalBottomBar
import com.vitaltrace.app.feature.treatments.presentation.TreatmentsMapper
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentCard
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelativePortalScreen(
    onLogout: () -> Unit,
    onEducationClick: (String, String) -> Unit,
    viewModel: RelativePortalViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it == RelativePortalEffect.NavigateToLogin) onLogout() }
    }

    val content = state as? RelativePortalUiState.Content
    if (showLogoutConfirmation) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutConfirmation = false
                viewModel.logout()
            },
            onDismiss = { showLogoutConfirmation = false }
        )
    }
    Scaffold(
        containerColor = VitalTraceWarmBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = content?.section?.title ?: "Portal familiar",
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showLogoutConfirmation = true }) {
                        Icon(Icons.AutoMirrored.Rounded.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitalTraceWarmBackground,
                    titleContentColor = VitalTraceNavy,
                    actionIconContentColor = VitalTraceNavy
                )
            )
        },
        bottomBar = {
            content?.let {
                RelativePortalBottomBar(it.section, viewModel::selectSection)
            }
        }
    ) { padding ->
        when (val current = state) {
            RelativePortalUiState.Loading -> LoadingState(
                Modifier.fillMaxSize().padding(padding)
            )
            RelativePortalUiState.NoAuthorizedPatients -> RelativeEmptyState(
                icon = Icons.Rounded.People,
                title = "Sin pacientes autorizados",
                description = "No tienes pacientes autorizados actualmente.",
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            is RelativePortalUiState.Error -> HomeErrorState(
                message = current.message,
                onRetryClick = viewModel::retry,
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            is RelativePortalUiState.Selecting -> PatientSelectorSheet(
                patients = current.patients,
                selectedPatientId = current.selectedPatientId,
                onSelect = viewModel::selectPatient,
                modifier = Modifier.fillMaxSize().padding(padding)
            )
            is RelativePortalUiState.Content -> RelativePortalContent(
                state = current,
                onChangePatient = viewModel::changePatient,
                onSelectSection = viewModel::selectSection,
                onEducationClick = onEducationClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun RelativePortalContent(
    state: RelativePortalUiState.Content,
    onChangePatient: () -> Unit,
    onSelectSection: (RelativeSection) -> Unit,
    onEducationClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (state.section) {
        RelativeSection.HOME -> RelativeHome(
            state = state,
            onChangePatient = onChangePatient,
            onMeasurementsClick = { onSelectSection(RelativeSection.MEASUREMENTS) },
            modifier = modifier
        )
        RelativeSection.APPOINTMENTS -> RelativeAppointments(state.portal, modifier)
        RelativeSection.MEASUREMENTS -> RelativeMeasurements(state.portal, modifier)
        RelativeSection.TREATMENTS -> RelativeTreatments(state.portal, modifier)
        RelativeSection.HISTORY -> ClinicalHistoryContent(
            history = state.portal.clinicalHistory,
            patientName = state.selected.fullName,
            onEducationClick = onEducationClick,
            modifier = modifier,
            showEducationActions = true
        )
    }
}

@Composable
private fun RelativeHome(
    state: RelativePortalUiState.Content,
    onChangePatient: () -> Unit,
    onMeasurementsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val home = remember(state.portal.summary, state.portal.measurements) {
        HomeSummaryMapper().map(state.portal.summary, state.portal.measurements)
    }
    val treatments = remember(state.portal.treatments) {
        TreatmentsMapper().map(state.portal.treatments).treatments
    }
    val nextAppointmentDetail = remember(state.portal.summary.nextAppointment) {
        state.portal.summary.nextAppointment?.let { appointment ->
            AppointmentsMapper().map(listOf(appointment)).nextAppointment?.toDetail()
        }
    }
    var selectedAppointment by remember(state.portal.summary.nextAppointment) {
        mutableStateOf<com.vitaltrace.app.feature.appointments.presentation.AppointmentDetailUiModel?>(null)
    }
    selectedAppointment?.let { detail ->
        AppointmentDetailSheet(detail = detail, onDismiss = { selectedAppointment = null })
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 22.dp, end = 24.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            ObservedPatientHeader(
                greeting = home.greeting,
                relativeName = state.relativeName,
                patient = state.selected,
                canChange = state.patients.size > 1,
                onChange = onChangePatient
            )
        }
        item {
            FollowUpStatusCard(
                status = home.followUpStatus?.copy(
                    title = "La última medición fue enviada para revisión."
                )
            )
        }
        item {
            NextAppointmentCard(
                appointment = home.nextAppointment,
                onDetailClick = { selectedAppointment = nextAppointmentDetail },
                showDetailAction = nextAppointmentDetail != null
            )
        }
        item {
            RecentPressureCard(
                measurement = home.recentMeasurement,
                onHistoryClick = onMeasurementsClick
            )
        }
        item { SectionTitle(Icons.Rounded.Medication, "Tratamientos activos") }
        if (treatments.isEmpty()) {
            item { CompactEmptyCard("No hay tratamientos activos.") }
        } else {
            items(treatments.take(3), key = { "home-treatment-${it.id}" }) {
                TreatmentCard(treatment = it, onClick = {})
            }
        }
    }
}

@Composable
private fun RelativeAppointments(content: RelativePortalContent, modifier: Modifier = Modifier) {
    val appointments = remember(content.appointments) {
        AppointmentsMapper().map(content.appointments)
    }
    var selectedAppointment by remember(content.appointments) {
        mutableStateOf<com.vitaltrace.app.feature.appointments.presentation.AppointmentDetailUiModel?>(null)
    }
    val showDetail: (Long) -> Unit = { appointmentId ->
        selectedAppointment = sequenceOf(appointments.nextAppointment)
            .plus(appointments.upcomingAppointments.asSequence())
            .plus(appointments.previousAppointments.asSequence())
            .filterNotNull()
            .firstOrNull { it.id == appointmentId }
            ?.toDetail()
    }
    selectedAppointment?.let { detail ->
        AppointmentDetailSheet(
            detail = detail,
            onDismiss = { selectedAppointment = null }
        )
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        appointments.nextAppointment?.let { next ->
            item { FeaturedAppointmentCard(next, onClick = { showDetail(next.id) }) }
        }
        item {
            AppointmentSection(
                title = "Próximas citas",
                appointments = appointments.upcomingAppointments,
                emptyMessage = "No hay citas próximas.",
                onAppointmentClick = showDetail
            )
        }
        item {
            AppointmentSection(
                title = "Citas anteriores",
                appointments = appointments.previousAppointments,
                emptyMessage = "No hay citas registradas.",
                onAppointmentClick = showDetail
            )
        }
    }
}

@Composable
private fun RelativeMeasurements(content: RelativePortalContent, modifier: Modifier = Modifier) {
    val measurements = remember(content.measurements) {
        MeasurementsMapper().map(content.measurements)
    }
    if (measurements.latestMeasurement == null) {
        RelativeEmptyState(
            Icons.Rounded.MonitorHeart,
            "Sin mediciones",
            "Aún no hay mediciones disponibles.",
            modifier.fillMaxSize()
        )
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item { LatestMeasurementCard(measurements.latestMeasurement, onClick = {}) }
        if (measurements.measurements.isNotEmpty()) {
            item { SectionTitle(Icons.Rounded.MonitorHeart, "Historial de mediciones") }
            item { MeasurementHistoryCard(measurements.measurements, onMeasurementClick = {}) }
        }
    }
}

@Composable
private fun RelativeTreatments(content: RelativePortalContent, modifier: Modifier = Modifier) {
    val treatments = remember(content.treatments) {
        TreatmentsMapper().map(content.treatments).treatments
    }
    if (treatments.isEmpty()) {
        RelativeEmptyState(
            Icons.Rounded.Medication,
            "Sin tratamientos",
            "No hay tratamientos activos.",
            modifier.fillMaxSize()
        )
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(treatments, key = { "treatment-${it.id}" }) {
            TreatmentCard(treatment = it, onClick = {})
        }
    }
}

@Composable
private fun ObservedPatientHeader(
    greeting: String,
    relativeName: String,
    patient: LinkedPatient,
    canChange: Boolean,
    onChange: () -> Unit
) {
    val displayedRelativeName = relativeName.ifBlank { "Familiar" }
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting, color = Color(0xFF53636D), fontWeight = FontWeight.SemiBold)
                    Text(
                        displayedRelativeName,
                        color = VitalTraceNavy,
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = VitalTraceTeal) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(initials(displayedRelativeName), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFE5E0D7))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Estás viendo a", color = VitalTraceTeal, fontWeight = FontWeight.Bold)
                    Text(
                        patient.fullName,
                        color = VitalTraceNavy,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (canChange) {
                    TextButton(onClick = onChange) {
                        Text("Cambiar", color = VitalTraceTeal, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PatientSelectorSheet(
    patients: List<LinkedPatient>,
    selectedPatientId: Long?,
    onSelect: (LinkedPatient) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text("Selecciona el paciente que deseas consultar.", color = VitalTraceNavy)
    }
    ModalBottomSheet(onDismissRequest = {}) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp)) {
            Text(
                "Seleccionar paciente",
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Solo aparecen pacientes con autorización activa.",
                color = Color(0xFF53636D),
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            patients.forEach { patient ->
                Card(
                    onClick = { onSelect(patient) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (patient.id == selectedPatientId) Color(0xFFD9F2F0) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(48.dp), shape = CircleShape, color = Color(0xFFD9F2F0)) {
                            Icon(Icons.Rounded.Person, null, Modifier.padding(12.dp), tint = VitalTraceTeal)
                        }
                        Column(Modifier.padding(start = 14.dp)) {
                            Text(patient.fullName, color = VitalTraceNavy, fontWeight = FontWeight.Bold)
                            Text(relationshipLabel(patient.relationship), color = Color(0xFF53636D))
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = VitalTraceTeal)
        Text(title, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 23.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CompactEmptyCard(message: String) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Text(message, Modifier.padding(22.dp), color = Color(0xFF53636D))
    }
}

@Composable
private fun RelativeEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, Modifier.size(72.dp), tint = VitalTraceMint)
        Text(
            title,
            Modifier.padding(top = 22.dp),
            color = VitalTraceNavy,
            fontFamily = FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            description,
            Modifier.padding(top = 12.dp),
            color = Color(0xFF53636D),
            textAlign = TextAlign.Center
        )
    }
}

private val RelativeSection.title: String
    get() = when (this) {
        RelativeSection.HOME -> "Inicio"
        RelativeSection.APPOINTMENTS -> "Citas"
        RelativeSection.MEASUREMENTS -> "Mediciones"
        RelativeSection.TREATMENTS -> "Tratamientos"
        RelativeSection.HISTORY -> "Historial clínico"
    }

private fun initials(name: String): String = name.trim().split(Regex("\\s+"))
    .filter(String::isNotBlank).take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")

private fun relationshipLabel(value: String): String = when (value.trim().uppercase()) {
    "DAUGHTER" -> "Hija"
    "SON" -> "Hijo"
    "MOTHER" -> "Madre"
    "FATHER" -> "Padre"
    "SISTER" -> "Hermana"
    "BROTHER" -> "Hermano"
    "SPOUSE" -> "Cónyuge"
    else -> value.lowercase().replaceFirstChar { it.titlecase() }
}
