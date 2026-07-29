package com.vitaltrace.app.feature.measurements.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.measurements.presentation.MeasurementDetailUiModel
import com.vitaltrace.app.feature.measurements.presentation.MeasurementFollowUpUiModel
import com.vitaltrace.app.feature.measurements.presentation.MeasurementStatus
import com.vitaltrace.app.feature.measurements.presentation.components.MeasurementStatusChip
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun MeasurementDetailContent(
    detail: MeasurementDetailUiModel,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unit = stringResource(R.string.measurements_pressure_unit)
    val valueDescription = stringResource(
        R.string.measurement_detail_value_description,
        detail.value,
        unit
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.measurement_detail_title),
                color = VitalTraceTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.7.sp
            )
            MeasurementStatusChip(status = detail.status)
        }
        Row(
            modifier = Modifier.semantics { contentDescription = valueDescription },
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = detail.value,
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 49.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                modifier = Modifier.padding(start = 12.dp, bottom = 7.dp),
                color = Color(0xFF53636D),
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        MeasurementInformationCard(detail = detail)
        detail.followUp?.let { followUp ->
            FollowUpCard(followUp = followUp)
        }
        OutlinedButton(
            onClick = onCloseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1DDD3)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = VitalTraceNavy)
        ) {
            Text(
                text = stringResource(R.string.measurement_detail_close),
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MeasurementDetailDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 17.dp, bottom = 20.dp)
            .width(64.dp)
            .height(7.dp)
    ) {
        Surface(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(50),
            color = Color(0xFFC9C2B5)
        ) {}
    }
}

@Composable
private fun MeasurementInformationCard(detail: MeasurementDetailUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            DetailRow(
                label = stringResource(R.string.measurement_detail_date),
                value = detail.date
            )
            HorizontalDivider(color = Color(0xFFE5E0D7))
            DetailRow(
                label = stringResource(R.string.measurement_detail_time),
                value = detail.time
            )
            HorizontalDivider(color = Color(0xFFE5E0D7))
            DetailRow(
                label = stringResource(R.string.measurement_detail_observation),
                value = detail.observation
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFF53636D), fontSize = 18.sp)
        Text(
            text = value,
            modifier = Modifier.padding(start = 20.dp),
            color = Color(0xFF172C3A),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun FollowUpCard(followUp: MeasurementFollowUpUiModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = VitalTraceMint.copy(alpha = 0.20f),
        contentColor = Color(0xFF172C3A),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.measurement_detail_follow_up),
                color = VitalTraceTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.6.sp
            )
            Text(
                text = followUp.message,
                fontSize = 18.sp,
                lineHeight = 27.sp
            )
            Text(
                text = "${followUp.date} · ${followUp.time}",
                color = Color(0xFF53636D),
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MeasurementDetailPreview() {
    VitalTraceTheme(dynamicColor = false) {
        Surface(color = VitalTraceWarmBackground) {
            MeasurementDetailContent(
                detail = MeasurementDetailUiModel(
                    id = "preview",
                    value = "145/92",
                    date = "14 jul 2026",
                    time = "9:42 a. m.",
                    observation = "En reposo",
                    status = MeasurementStatus.REVIEWED,
                    followUp = MeasurementFollowUpUiModel(
                        message = "Dr. Carlos Ruiz revisó el registro y dejó una observación de seguimiento.",
                        date = "15 jul",
                        time = "11:20 a. m."
                    )
                ),
                onCloseClick = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
