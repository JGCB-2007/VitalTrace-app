package com.vitaltrace.app.feature.treatments.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.treatments.presentation.TreatmentUiModel
import com.vitaltrace.app.feature.treatments.presentation.components.TreatmentStatusChip
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreatmentDetailSheet(
    treatment: TreatmentUiModel,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = treatment.diagnosisDescription.orEmpty().ifBlank {
                        stringResource(R.string.treatment_detail_title)
                    },
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = FontFamily.Serif,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )
                TreatmentStatusChip(treatment.status)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    TreatmentRow(stringResource(R.string.treatment_detail_indications), treatment.indications)
                    treatment.diagnosisCode?.takeIf(String::isNotBlank)?.let {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        TreatmentRow(stringResource(R.string.treatment_detail_diagnosis_code), it)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    TreatmentRow(stringResource(R.string.treatment_detail_start_date), treatment.startDate)
                    treatment.endDate?.takeIf(String::isNotBlank)?.let {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        TreatmentRow(stringResource(R.string.treatment_detail_end_date), it)
                    }
                    treatment.prescriberName?.takeIf(String::isNotBlank)?.let {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        TreatmentRow(stringResource(R.string.treatment_detail_prescriber), it)
                    }
                }
            }
        }
    }
}

@Composable
private fun TreatmentRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            modifier = Modifier.weight(1f).padding(start = 18.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}
