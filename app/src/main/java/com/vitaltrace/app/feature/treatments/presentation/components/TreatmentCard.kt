package com.vitaltrace.app.feature.treatments.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.treatments.presentation.TreatmentUiModel
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun TreatmentCard(
    treatment: TreatmentUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFFDDF4F2), shape = RoundedCornerShape(18.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Medication,
                        contentDescription = null,
                        modifier = Modifier.padding(14.dp),
                        tint = VitalTraceTeal
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                    treatment.diagnosisDescription?.takeIf(String::isNotBlank)?.let {
                        Text(
                            text = it,
                            color = VitalTraceNavy,
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(text = treatment.indications, color = Color(0xFF53636D), fontSize = 16.sp)
                }
                TreatmentStatusChip(status = treatment.status)
            }
            Text(
                text = listOfNotNull(treatment.startDate, treatment.endDate).joinToString("  ·  "),
                color = Color(0xFF53636D),
                fontSize = 15.sp
            )
        }
    }
}
