package com.vitaltrace.app.feature.relativeportal.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.ui.theme.SoraFontFamily
import com.vitaltrace.app.AppShellViewModel
import com.vitaltrace.app.core.presentation.components.ObservedPatientHeader
import com.vitaltrace.app.core.presentation.components.OfflineStatusBanner
import com.vitaltrace.app.core.presentation.components.ThemePreferenceCard
import com.vitaltrace.app.feature.appointments.presentation.AppointmentsMapper
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentSection
import com.vitaltrace.app.feature.appointments.presentation.components.FeaturedAppointmentCard
import com.vitaltrace.app.feature.appointments.presentation.detail.AppointmentDetailSheet
import com.vitaltrace.app.feature.clinicalhistory.presentation.ClinicalHistoryContent
import com.vitaltrace.app.feature.home.presentation.HomeSummaryMapper
import com.vitaltrace.app.feature.home.presentation.components.FollowUpStatusCard
import com.vitaltrace.app.feature.home.presentation.components.HealthOverviewCard
import com.vitaltrace.app.feature.home.presentation.components.HomeErrorState
import com.vitaltrace.app.feature.home.presentation.components.LoadingState
import com.vitaltrace.app.feature.home.presentation.components.LogoutConfirmationDialog
import com.vitaltrace.app.feature.home.presentation.components.NextAppointmentCard
import com.vitaltrace.app.feature.home.presentation.components.RecentPressureCard
import com.vitaltrace.app.feature.measurements.presentation.MeasurementsMapper
import com.vitaltrace.app.feature.measurements.presentation.MeasurementUiModel
import com.vitaltrace.app.feature.measurements.presentation.MeasurementPeriod
import com.vitaltrace.app.feature.measurements.presentation.measuredDateTime
import com.vitaltrace.app.feature.measurements.presentation.components.LatestMeasurementCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementInsightsCard
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementHistoryCard
import com.vitaltrace.app.feature.measurements.presentation.detail.MeasurementDetailSheet
import com.vitaltrace.app.feature.relativeportal.domain.model.LinkedPatient
import com.vitaltrace.app.feature.relativeportal.presentation.components.RelativePortalBottomBar
import com.vitaltrace.app.feature.treatments.presentation.TreatmentsMapper
import com.vitaltrace.app.feature.treatments.presentation.TreatmentUiModel
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentCard
import com.vitaltrace.app.feature.treatments.presentation.detail.TreatmentDetailSheet
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelativePortalScreen(
    onLogout: () -> Unit,
    onEducationClick: (String, String) -> Unit,
    viewModel: RelativePortalViewModel = hiltViewModel(),
    appShellViewModel: AppShellViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appShellState by appShellViewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    var showThemeSettings by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it == RelativePortalEffect.NavigateToLogin) onLogout() }
    }

    val content = state as? RelativePortalUiState.Content
    BackHandler(enabled = content?.section != null && content.section != RelativeSection.HOME) {
        viewModel.selectSection(RelativeSection.HOME)
    }
    if (showLogoutConfirmation) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutConfirmation = false
                viewModel.logout()
            },
            onDismiss = { showLogoutConfirmation = false }
        )
    }
    if (showThemeSettings) {
        ModalBottomSheet(onDismissRequest = { showThemeSettings = false }) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Apariencia",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = SoraFontFamily,
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = content?.section?.title ?: "Portal familiar",
                        fontFamily = SoraFontFamily,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (content != null) {
                        IconButton(onClick = { showThemeSettings = true }) {
                            Icon(Icons.Rounded.Palette, "Cambiar tema")
                        }
                    }
                    IconButton(onClick = { showLogoutConfirmation = true }) {
                        Icon(Icons.AutoMirrored.Rounded.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
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
            is RelativePortalUiState.Content -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                RelativePortalContent(
                    state = current,
                    onChangePatient = viewModel::changePatient,
                    onSelectSection = viewModel::selectSection,
                    onEducationClick = onEducationClick,
                    modifier = Modifier.fillMaxSize().widthIn(max = 720.dp)
                )
            }
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
    Column(modifier) {
        if (state.section != RelativeSection.HOME) {
            RelativePatientContextCard(
                patientName = state.selected.fullName,
                canChange = state.patients.size > 1,
                onChange = onChangePatient,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
            )
        }
        AnimatedContent(
            targetState = state.section,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                fadeIn(tween(190)) togetherWith fadeOut(tween(120))
            },
            label = "relative-portal-section"
        ) { section ->
            val sectionModifier = Modifier.fillMaxSize()
            when (section) {
                RelativeSection.HOME -> RelativeHome(
                    state = state,
                    onChangePatient = onChangePatient,
                    onMeasurementsClick = { onSelectSection(RelativeSection.MEASUREMENTS) },
                    onHistoryClick = { onSelectSection(RelativeSection.HISTORY) },
                    modifier = sectionModifier
                )
                RelativeSection.APPOINTMENTS -> RelativeAppointments(state.portal, sectionModifier)
                RelativeSection.MEASUREMENTS -> RelativeMeasurements(state.portal, sectionModifier)
                RelativeSection.TREATMENTS -> RelativeTreatments(state.portal, sectionModifier)
                RelativeSection.HISTORY -> ClinicalHistoryContent(
                    history = state.portal.clinicalHistory,
                    patientName = state.selected.fullName,
                    onEducationClick = onEducationClick,
                    modifier = sectionModifier,
                    showEducationActions = true
                )
            }
        }
    }
}

@Composable
private fun RelativeHome(
    state: RelativePortalUiState.Content,
    onChangePatient: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val home = remember(state.portal.summary, state.portal.measurements) {
        HomeSummaryMapper().map(state.portal.summary, state.portal.measurements)
    }
    val treatments = remember(state.portal.treatments) {
        TreatmentsMapper().map(state.portal.treatments).treatments
    }
    var selectedTreatment by remember(state.portal.treatments) {
        mutableStateOf<TreatmentUiModel?>(null)
    }
    selectedTreatment?.let { treatment ->
        TreatmentDetailSheet(treatment, onDismiss = { selectedTreatment = null })
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
        item { OfflineStatusBanner() }
        item {
            ObservedPatientHeader(
                greeting = home.greeting,
                authenticatedName = state.relativeName,
                patientName = state.selected.fullName,
                canChange = state.patients.size > 1,
                onChange = onChangePatient
            )
        }
        item { RelativeReadOnlyNotice() }
        item {
            HealthOverviewCard(
                status = home.healthStatus,
                activeTreatmentsCount = home.activeTreatmentsCount,
                onTimelineClick = onHistoryClick
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
                TreatmentCard(treatment = it, onClick = { selectedTreatment = it })
            }
        }
    }
}

@Composable
private fun RelativePatientContextCard(
    patientName: String,
    canChange: Boolean,
    onChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                Icon(Icons.Rounded.Person, null, Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.secondary)
            }
            Column(Modifier.weight(1f)) {
                Text("Paciente consultado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                Text(patientName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Acceso de solo lectura", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canChange) TextButton(onClick = onChange) { Text("Cambiar", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun RelativeReadOnlyNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Icon(Icons.Rounded.Visibility, null, tint = MaterialTheme.colorScheme.secondary)
            Column {
                Text("Consulta familiar", color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Bold)
                Text(
                    "Puedes consultar la información autorizada sin modificar datos clínicos.",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.bodySmall
                )
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
    val allMeasurements = remember(measurements) {
        (listOfNotNull(measurements.latestMeasurement) + measurements.measurements)
            .distinctBy(MeasurementUiModel::id)
    }
    val availableTypes = remember(allMeasurements) {
        allMeasurements.map(MeasurementUiModel::typeName).distinct().sorted()
    }
    var selectedType by remember(content.measurements) { mutableStateOf<String?>(null) }
    var selectedPeriod by remember { mutableStateOf(MeasurementPeriod.DAYS_30) }
    val chartMeasurements = remember(allMeasurements, selectedType, selectedPeriod) {
        val type = selectedType ?: availableTypes.firstOrNull()
        val threshold = selectedPeriod.takeUnless { it == MeasurementPeriod.ALL }
            ?.let { LocalDateTime.now().minusDays(it.days.toLong()) }
        allMeasurements.filter {
            it.typeName == type &&
                (threshold == null || it.measuredDateTime()?.isAfter(threshold) != false)
        }.sortedBy(MeasurementUiModel::measuredAtRaw)
    }
    var selectedMeasurement by remember(content.measurements) {
        mutableStateOf<MeasurementUiModel?>(null)
    }
    selectedMeasurement?.let { measurement ->
        MeasurementDetailSheet(
            detail = measurement.toDetail(),
            onDismiss = { selectedMeasurement = null }
        )
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
        item {
            MeasurementInsightsCard(
                measurements = chartMeasurements,
                availableTypes = availableTypes,
                selectedType = selectedType,
                selectedPeriod = selectedPeriod,
                onTypeSelected = { selectedType = it },
                onPeriodSelected = { selectedPeriod = it }
            )
        }
        item {
            LatestMeasurementCard(
                measurements.latestMeasurement,
                onClick = { selectedMeasurement = measurements.latestMeasurement }
            )
        }
        if (measurements.measurements.isNotEmpty()) {
            item { SectionTitle(Icons.Rounded.MonitorHeart, "Historial de mediciones") }
            item {
                MeasurementHistoryCard(
                    measurements.measurements,
                    onMeasurementClick = { id ->
                        selectedMeasurement = measurements.measurements.firstOrNull { it.id == id }
                    }
                )
            }
        }
    }
}

@Composable
private fun RelativeTreatments(content: RelativePortalContent, modifier: Modifier = Modifier) {
    val treatments = remember(content.treatments) {
        TreatmentsMapper().map(content.treatments).treatments
    }
    var selectedTreatment by remember(content.treatments) { mutableStateOf<TreatmentUiModel?>(null) }
    selectedTreatment?.let { treatment ->
        TreatmentDetailSheet(treatment, onDismiss = { selectedTreatment = null })
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
            TreatmentCard(treatment = it, onClick = { selectedTreatment = it })
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
        Text("Selecciona el paciente que deseas consultar.", color = MaterialTheme.colorScheme.onBackground)
    }
    ModalBottomSheet(onDismissRequest = {}) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp)) {
            Text(
                "Seleccionar paciente",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = SoraFontFamily,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Solo aparecen pacientes con autorización activa.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
            )
            patients.forEach { patient ->
                Card(
                    onClick = { onSelect(patient) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (patient.id == selectedPatientId) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                            Icon(Icons.Rounded.Person, null, Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.secondary)
                        }
                        Column(Modifier.padding(start = 14.dp)) {
                            Text(patient.fullName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            Text(relationshipLabel(patient.relationship), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary)
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontFamily = SoraFontFamily, fontSize = 23.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CompactEmptyCard(message: String) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Text(message, Modifier.padding(22.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Icon(icon, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.tertiary)
        Text(
            title,
            Modifier.padding(top = 22.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = SoraFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            description,
            Modifier.padding(top = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
