package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.measurements.presentation.MeasurementFilter
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
fun MeasurementFilterBar(
    selectedFilter: MeasurementFilter,
    onFilterSelected: (MeasurementFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        MeasurementFilterOption(MeasurementFilter.ALL, R.string.measurements_filter_all),
        MeasurementFilterOption(MeasurementFilter.PENDING, R.string.measurements_filter_pending),
        MeasurementFilterOption(MeasurementFilter.REVIEWED, R.string.measurements_filter_reviewed)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFECE8E0),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                val selected = option.filter == selectedFilter
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onFilterSelected(option.filter) },
                    color = if (selected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(14.dp),
                    shadowElevation = if (selected) 2.dp else 0.dp
                ) {
                    Text(
                        text = stringResource(option.label),
                        modifier = Modifier.padding(vertical = 13.dp, horizontal = 4.dp),
                        color = if (selected) VitalTraceNavy else Color(0xFF68747B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private data class MeasurementFilterOption(
    val filter: MeasurementFilter,
    @param:StringRes val label: Int
)
