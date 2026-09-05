package com.vitaltrace.app.feature.treatments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R
import com.vitaltrace.app.core.presentation.localization.EnumDisplayEs
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentStatusChip
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: TreatmentsViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val treatment = state.selectedTreatment
    Scaffold(
        containerColor = VitalTraceWarmBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.treatment_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            stringResource(R.string.treatment_detail_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        treatment?.let {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    it.diagnosisDescription?.takeIf(String::isNotBlank)?.let { description ->
                        Text(
                            text = description,
                            modifier = Modifier.weight(1f),
                            color = VitalTraceNavy,
                            fontFamily = FontFamily.Serif,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TreatmentStatusChip(status = it.status)
                }
                TreatmentDetailsCard(it)
            }
        }
    }
}

@Composable
private fun TreatmentDetailsCard(treatment: TreatmentUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            DetailRow(stringResource(R.string.treatment_detail_indications), treatment.indications)
            treatment.diagnosisCode?.takeIf(String::isNotBlank)?.let {
                HorizontalDivider(color = Color(0xFFE5E0D7))
                DetailRow(stringResource(R.string.treatment_detail_diagnosis_code), it)
            }
            HorizontalDivider(color = Color(0xFFE5E0D7))
            DetailRow(stringResource(R.string.treatment_detail_start_date), treatment.startDate)
            treatment.endDate?.takeIf(String::isNotBlank)?.let {
                HorizontalDivider(color = Color(0xFFE5E0D7))
                DetailRow(stringResource(R.string.treatment_detail_end_date), it)
            }
            treatment.prescriberName?.takeIf(String::isNotBlank)?.let {
                HorizontalDivider(color = Color(0xFFE5E0D7))
                DetailRow(stringResource(R.string.treatment_detail_prescriber), it)
            }
            treatment.professionalType?.takeIf(String::isNotBlank)?.let {
                HorizontalDivider(color = Color(0xFFE5E0D7))
                DetailRow(
                    stringResource(R.string.treatment_detail_professional_type),
                    EnumDisplayEs.professionalType(it)
                )
            }
            treatment.specialtyName?.takeIf(String::isNotBlank)?.let {
                HorizontalDivider(color = Color(0xFFE5E0D7))
                DetailRow(stringResource(R.string.treatment_detail_specialty), it)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 17.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF53636D), fontSize = 17.sp)
        Text(
            value,
            modifier = Modifier.weight(1f).padding(start = 20.dp),
            color = VitalTraceNavy,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}
