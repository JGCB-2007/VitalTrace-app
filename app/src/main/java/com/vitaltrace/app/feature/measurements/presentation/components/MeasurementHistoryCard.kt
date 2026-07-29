package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.measurements.presentation.MeasurementUiModel
import com.vitaltrace.app.feature.measurements.presentation.MeasurementStatus
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun MeasurementHistoryCard(
    measurements: List<MeasurementUiModel>,
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
    modifier: Modifier = Modifier
) {
    val unit = stringResource(R.string.measurements_pressure_unit)
    val statusLabel = stringResource(measurement.status.labelResource())
    val description = stringResource(
        R.string.measurements_item_description,
        measurement.value,
        unit,
        "${measurement.date} ${measurement.time}",
        statusLabel
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            color = Color(0xFFDDF4F2),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.MonitorHeart,
                contentDescription = null,
                tint = VitalTraceTeal,
                modifier = Modifier.padding(15.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = measurement.value,
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                color = VitalTraceNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${measurement.date} · ${measurement.time}",
                color = Color(0xFF53636D),
                fontSize = 16.sp
            )
        }
        MeasurementStatusChip(status = measurement.status)
    }
}

private fun MeasurementStatus.labelResource(): Int {
    return when (this) {
        MeasurementStatus.REGISTERED -> R.string.measurements_status_registered
        MeasurementStatus.PENDING -> R.string.measurements_status_pending
        MeasurementStatus.IN_REVIEW -> R.string.measurements_status_in_review
        MeasurementStatus.REVIEWED -> R.string.measurements_status_reviewed
    }
}
