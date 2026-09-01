package com.vitaltrace.app.feature.clinicalhistory.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.vitaltrace.app.feature.patient.domain.model.*
import com.vitaltrace.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClinicalHistoryDesign(
    state: ClinicalHistoryUiState,
    onNavigateBack: () -> Unit,
    onEducationClick: (String, String) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        containerColor = VitalTraceWarmBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial clínico",
                        fontFamily = FontFamily.Serif,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitalTraceWarmBackground,
                    titleContentColor = VitalTraceNavy,
                    navigationIconContentColor = VitalTraceNavy
                )
            )
        }
    ) { padding ->
        when (state) {
            ClinicalHistoryUiState.Loading -> ClinicalLoading(Modifier.padding(padding))
            is ClinicalHistoryUiState.Empty -> ClinicalEmpty(Modifier.padding(padding))
            is ClinicalHistoryUiState.Error -> ClinicalError(state.message, onRetry, Modifier.padding(padding))
            is ClinicalHistoryUiState.Success -> ClinicalHistoryContent(state.history, onEducationClick, Modifier.padding(padding))
        }
    }
}

@Composable
internal fun ClinicalHistoryContent(
    history: ClinicalHistory,
    onEducationClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    showEducationActions: Boolean = true
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { RecordBadge(history.recordNumber) }
        if (history.diagnoses.isNotEmpty()) {
            item { ClinicalSectionHeader(Icons.Rounded.HealthAndSafety, "Diagnósticos") }
            items(history.diagnoses, key = { "diagnosis-${it.id}" }) {
                DiagnosisCard(it, onEducationClick, showEducationActions)
            }
        }
        if (history.clinicalEvolutions.isNotEmpty()) {
            item { ClinicalSectionHeader(Icons.Rounded.HistoryEdu, "Evoluciones clínicas") }
            items(history.clinicalEvolutions, key = { "evolution-${it.id}" }) { EvolutionCard(it) }
        }
        if (history.currentTreatments.isNotEmpty()) {
            item { ClinicalSectionHeader(Icons.Rounded.Medication, "Tratamientos actuales") }
            items(history.currentTreatments, key = { "treatment-${it.id}" }) { ClinicalTreatmentCard(it) }
        }
        if (history.recentMeasurements.isNotEmpty()) {
            item { ClinicalSectionHeader(Icons.Rounded.MonitorHeart, "Mediciones recientes") }
            item { ClinicalMeasurementsCard(history.recentMeasurements) }
        }
    }
}

@Composable
private fun RecordBadge(recordNumber: String) {
    Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 3.dp) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = Color(0xFFDDF4F2), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Rounded.FolderShared, null, Modifier.padding(11.dp), tint = VitalTraceTeal)
            }
            Column {
                Text("Expediente", color = Color(0xFF53636D), fontSize = 13.sp)
                Text(recordNumber, color = VitalTraceNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ClinicalSectionHeader(icon: ImageVector, title: String) {
    Row(
        Modifier.padding(top = 10.dp, bottom = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = VitalTraceTeal)
        Text(title, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 23.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DiagnosisCard(
    item: ClinicalDiagnosis,
    onEducationClick: (String, String) -> Unit,
    showEducationAction: Boolean
) = VitalTraceClinicalCard {
    Text(item.description, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        item.cieCode?.takeIf(String::isNotBlank)?.let { DetailLabel("Código CIE", it) }
        ClinicalStatusChip(item.status)
    }
    DetailLine(Icons.Rounded.CalendarMonth, item.diagnosisDate)
    professionalName(item.professional)?.let { DetailLine(Icons.Rounded.MedicalServices, it) }
    item.cieCode?.takeIf(String::isNotBlank)?.takeIf { showEducationAction }?.let { code ->
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { onEducationClick(code, item.description) },
            color = Color(0xFFF3FAF9),
            contentColor = VitalTraceTeal,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, VitalTraceMint.copy(alpha = 0.55f))
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Rounded.MenuBook, null, Modifier.size(20.dp))
                Text("Ver información educativa", Modifier.weight(1f).padding(horizontal = 10.dp), fontWeight = FontWeight.Bold)
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, Modifier.size(19.dp))
            }
        }
    }
}

@Composable
private fun EvolutionCard(item: ClinicalEvolution) {
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }
    VitalTraceClinicalCard(Modifier.animateContentSize()) {
        Text(
            item.clinicalSummary,
            color = VitalTraceNavy,
            fontFamily = FontFamily.Serif,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            maxLines = if (expanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis
        )
        if (item.clinicalSummary.length > 180) {
            Text(
                if (expanded) "Ver menos" else "Ver más",
                modifier = Modifier.clickable { expanded = !expanded },
                color = VitalTraceTeal,
                fontWeight = FontWeight.Bold
            )
        }
        DetailLine(Icons.Rounded.CalendarMonth, item.recordedAt)
        professionalName(item.professional)?.let { DetailLine(Icons.Rounded.MedicalServices, it) }
        ClinicalStatusChip(item.status)
    }
}

@Composable
private fun ClinicalTreatmentCard(item: ClinicalTreatment) = VitalTraceClinicalCard {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(color = Color(0xFFDDF4F2), shape = RoundedCornerShape(18.dp)) {
            Icon(Icons.Rounded.Medication, null, Modifier.padding(14.dp), tint = VitalTraceTeal)
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(item.indications, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(listOfNotNull(item.startDate, item.endDate).joinToString("  ·  "), color = Color(0xFF53636D), fontSize = 15.sp)
        }
        ClinicalStatusChip(item.status)
    }
    professionalName(item.professional)?.let { DetailLine(Icons.Rounded.MedicalServices, it) }
}

@Composable
private fun ClinicalMeasurementsCard(items: List<ClinicalMeasurement>) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
            items.forEachIndexed { index, item ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(Modifier.size(54.dp), color = Color(0xFFDDF4F2), shape = RoundedCornerShape(17.dp)) {
                        Icon(Icons.Rounded.MonitorHeart, null, Modifier.padding(14.dp), tint = VitalTraceTeal)
                    }
                    Column(Modifier.weight(1f)) {
                        Text(item.measurementType?.name ?: "Medición", color = VitalTraceNavy, fontWeight = FontWeight.Bold)
                        Text("${item.value} ${item.unit}", color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text(item.measuredAt, color = Color(0xFF53636D), fontSize = 14.sp)
                    }
                }
                if (index < items.lastIndex) HorizontalDivider(color = Color(0xFFE5E0D7))
            }
        }
    }
}

@Composable
private fun VitalTraceClinicalCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun ClinicalStatusChip(status: String) {
    val (background, content) = when (status) {
        "ACTIVE", "STABLE", "RECOVERY" -> Color(0xFFDDF1E7) to Color(0xFF23805F)
        "RESOLVED" -> Color(0xFFDDF4F2) to VitalTraceTeal
        "CRITICAL", "DELICATE" -> Color(0xFFF3E4E1) to Color(0xFF8C3D32)
        else -> Color(0xFFE8E8E8) to Color(0xFF53636D)
    }
    Surface(color = background, contentColor = content, shape = RoundedCornerShape(50)) {
        Text(statusLabel(status), Modifier.padding(horizontal = 13.dp, vertical = 7.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable private fun DetailLine(icon: ImageVector, value: String) = Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, null, Modifier.size(18.dp), tint = VitalTraceTeal)
    Text(value, color = Color(0xFF53636D), fontSize = 15.sp)
}

@Composable private fun DetailLabel(label: String, value: String) = Column {
    Text(label, color = Color(0xFF53636D), fontSize = 12.sp)
    Text(value, color = VitalTraceNavy, fontWeight = FontWeight.Bold)
}

@Composable private fun ClinicalLoading(modifier: Modifier) = Column(
    modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
) {
    CircularProgressIndicator(color = VitalTraceTeal)
    Text("Cargando historial clínico", Modifier.padding(top = 16.dp), color = VitalTraceNavy)
}

@Composable private fun ClinicalEmpty(modifier: Modifier) = Column(
    modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
) {
    Icon(Icons.Rounded.FolderShared, null, Modifier.size(72.dp), tint = VitalTraceMint)
    Text("Aún no existe información clínica registrada.", Modifier.padding(top = 22.dp), color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 25.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
}

@Composable private fun ClinicalError(message: String, onRetry: () -> Unit, modifier: Modifier) = Column(
    modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
) {
    Icon(Icons.Rounded.MedicalInformation, null, tint = VitalTraceTeal)
    Text(message, Modifier.padding(top = 14.dp), color = VitalTraceNavy, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    Button(
        onClick = onRetry,
        modifier = Modifier.fillMaxWidth().padding(top = 22.dp).height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)
    ) { Text("Reintentar", fontFamily = FontFamily.Serif, fontSize = 19.sp, fontWeight = FontWeight.Bold) }
}

private fun professionalName(value: ClinicalProfessional?) = value?.fullName?.takeIf(String::isNotBlank)
private fun statusLabel(value: String) = when (value) {
    "ACTIVE" -> "Activo"; "RESOLVED" -> "Resuelto"; "UNDER_REVIEW" -> "En revisión"
    "STABLE" -> "Estable"; "OBSERVATION" -> "En observación"; "DELICATE" -> "Delicado"
    "CRITICAL" -> "Crítico"; "RECOVERY" -> "En recuperación"; else -> value
}
