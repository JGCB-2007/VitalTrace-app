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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.vitaltrace.app.feature.measurements.presentation.MeasurementDetailUiModel
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun MeasurementDetailContent(
    detail: MeasurementDetailUiModel,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(
            text = stringResource(R.string.measurement_detail_title),
            color = VitalTraceTeal,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.7.sp
        )
        Text(text = detail.typeName, color = Color(0xFF53636D), fontSize = 18.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = detail.value,
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 49.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = detail.unit,
                modifier = Modifier.padding(start = 12.dp, bottom = 7.dp),
                color = Color(0xFF53636D),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        MeasurementInformationCard(detail)
        OutlinedButton(
            onClick = onCloseClick,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(18.dp),
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
    Box(modifier = Modifier.padding(top = 17.dp, bottom = 20.dp).width(64.dp).height(7.dp)) {
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
            DetailRow(stringResource(R.string.measurement_detail_date), detail.date)
            HorizontalDivider(color = Color(0xFFE5E0D7))
            DetailRow(stringResource(R.string.measurement_detail_time), detail.time)
            HorizontalDivider(color = Color(0xFFE5E0D7))
            DetailRow(stringResource(R.string.measurement_detail_observation), detail.observation)
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
        Text(text = label, color = Color(0xFF53636D), fontSize = 18.sp)
        Text(
            text = value,
            modifier = Modifier.weight(1f).padding(start = 20.dp),
            color = Color(0xFF172C3A),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}
