package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.measurements.presentation.MeasurementUiModel
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun LatestMeasurementCard(
    measurement: MeasurementUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, VitalTraceMint),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.measurements_latest),
                    color = VitalTraceTeal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                MeasurementStatusChip(status = measurement.status)
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = measurement.value,
                    color = VitalTraceNavy,
                    fontFamily = FontFamily.Serif,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.measurements_pressure_unit),
                    modifier = Modifier.padding(start = 10.dp, bottom = 4.dp),
                    color = Color(0xFF53636D),
                    fontSize = 20.sp
                )
            }
            Text(
                text = "${measurement.date} · ${measurement.time}",
                color = Color(0xFF53636D),
                fontSize = 18.sp
            )
        }
    }
}
