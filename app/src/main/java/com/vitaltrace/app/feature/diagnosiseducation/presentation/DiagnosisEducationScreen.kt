package com.vitaltrace.app.feature.diagnosiseducation.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducation
import com.vitaltrace.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisEducationScreen(
    onNavigateBack: () -> Unit,
    viewModel: DiagnosisEducationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        containerColor = VitalTraceWarmBackground,
        topBar = {
            TopAppBar(
                title = { Text("Información educativa", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VitalTraceWarmBackground,
                    titleContentColor = VitalTraceNavy,
                    navigationIconContentColor = VitalTraceNavy
                )
            )
        }
    ) { padding ->
        when (val content = state) {
            DiagnosisEducationUiState.Loading -> EducationMessage("Cargando información educativa", null, Modifier.padding(padding))
            DiagnosisEducationUiState.Error -> EducationMessage(
                "No pudimos cargar la información educativa en este momento.",
                viewModel::retry,
                Modifier.padding(padding)
            )
            is DiagnosisEducationUiState.Empty -> EducationEmpty(content.education, Modifier.padding(padding))
            is DiagnosisEducationUiState.Success -> EducationContent(content.education, Modifier.padding(padding))
        }
    }
}

@Composable
private fun EducationContent(education: DiagnosisEducation, modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { EducationHeader(education) }
        item { EducationalNotice() }
        items(education.items, key = { it.url }) { item ->
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(item.title, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(item.summary, color = Color(0xFF53636D), fontSize = 16.sp, lineHeight = 24.sp)
                    Button(
                        onClick = { runCatching { uriHandler.openUri(item.url) } },
                        colors = ButtonDefaults.buttonColors(containerColor = VitalTraceTeal),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Leer más", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun EducationHeader(education: DiagnosisEducation) = Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(education.diagnosisName, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    education.cieCode?.takeIf(String::isNotBlank)?.let { Text("Código CIE: $it", color = Color(0xFF53636D)) }
    Surface(color = Color(0xFFDDF4F2), contentColor = VitalTraceTeal, shape = RoundedCornerShape(50)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.AutoMirrored.Rounded.MenuBook, null, Modifier.size(18.dp))
            Text("Información de ${education.source}", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EducationalNotice() = Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
    Text(
        "Esta información es educativa y no sustituye la valoración de un profesional de la salud.",
        Modifier.padding(18.dp),
        color = VitalTraceNavy,
        fontSize = 15.sp,
        lineHeight = 21.sp
    )
}

@Composable
private fun EducationEmpty(education: DiagnosisEducation, modifier: Modifier) = Column(
    modifier.fillMaxSize().padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Icon(Icons.AutoMirrored.Rounded.MenuBook, null, Modifier.size(72.dp), tint = VitalTraceMint)
    Text(education.diagnosisName, Modifier.padding(top = 18.dp), color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 23.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    Text("No se encontró información educativa para este diagnóstico.", Modifier.padding(top = 12.dp), color = Color(0xFF53636D), textAlign = TextAlign.Center)
}

@Composable
private fun EducationMessage(message: String, retry: (() -> Unit)?, modifier: Modifier) = Column(
    modifier.fillMaxSize().padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    if (retry == null) CircularProgressIndicator(color = VitalTraceTeal) else Icon(Icons.AutoMirrored.Rounded.MenuBook, null, tint = VitalTraceTeal)
    Text(message, Modifier.padding(top = 16.dp), color = VitalTraceNavy, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    retry?.let {
        Button(onClick = it, Modifier.padding(top = 20.dp), colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)) { Text("Reintentar") }
    }
}
