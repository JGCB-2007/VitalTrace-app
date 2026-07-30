package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.measurements.presentation.MeasurementUiModel
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.core.presentation.localizedMeasurementTypeLabel
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MeasurementHistoryCard(
    measurements: List<MeasurementUiModel>,
    onMeasurementClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
            measurements.forEachIndexed { index, measurement ->
                MeasurementHistoryItem(
                    measurement = measurement,
                    onClick = { onMeasurementClick(measurement.id) },
                    modifier = Modifier.padding(vertical = 18.dp)
                )
                if (index < measurements.lastIndex) {
                    HorizontalDivider(color = Color(0xFFE5E0D7))
                }
            }
        }
    }
}

@Composable
private fun MeasurementHistoryItem(
    measurement: MeasurementUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = "${measurement.typeName}: ${measurement.value} ${measurement.unit}"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            color = Color(0xFFDDF4F2),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MonitorHeart,
                contentDescription = null,
                tint = VitalTraceTeal,
                modifier = Modifier.padding(13.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${measurement.value} ${measurement.unit}",
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = localizedMeasurementTypeLabel(measurement.typeName),
                color = Color(0xFF53636D),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${measurement.date} · ${measurement.time}",
                color = Color(0xFF53636D),
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        MeasurementStatusChip(status = measurement.status)
    }
}
