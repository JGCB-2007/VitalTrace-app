package com.vitaltrace.app.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.RecentMeasurementUiModel

@Composable
fun RecentPressureCard(
    measurement: RecentMeasurementUiModel?,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.home_recent_pressure),
                    color = HomeTeal,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onHistoryClick) {
                    Text(
                        text = stringResource(R.string.home_history),
                        color = HomeTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (measurement == null) {
                Text(
                    text = stringResource(R.string.home_no_measurements),
                    color = HomeSupportingText,
                    modifier = Modifier.padding(top = 14.dp)
                )
                return@Column
            }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = measurement.value,
                    color = HomeNavy,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.displayMedium
                )
                Text(
                    text = stringResource(
                        R.string.home_measurement_summary,
                        measurement.unit,
                        measurement.date
                    ),
                    color = HomeSupportingText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 10.dp, bottom = 8.dp)
                )
            }
            PressureBarChart(
                values = measurement.chartValues,
                modifier = Modifier.padding(top = 22.dp)
            )
        }
    }
}

@Composable
private fun PressureBarChart(
    values: List<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        values.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(82.dp * value.coerceIn(0.1f, 1f))
                    .background(
                        brush = if (index == values.lastIndex) {
                            Brush.verticalGradient(
                                listOf(Color(0xFFF2BD62), HomeAlertBar)
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF71D7D2),
                                    Color(0xFF2C9FA4)
                                )
                            )
                        },
                        shape = RoundedCornerShape(
                            topStart = 7.dp,
                            topEnd = 7.dp
                        )
                    )
            )
        }
    }
}
