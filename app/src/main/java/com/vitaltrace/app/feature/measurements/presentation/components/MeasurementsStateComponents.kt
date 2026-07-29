package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun MeasurementsLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = VitalTraceTeal)
        Text(
            text = stringResource(R.string.measurements_loading),
            modifier = Modifier.padding(top = 16.dp),
            color = VitalTraceNavy
        )
    }
}

@Composable
fun MeasurementsErrorState(
    message: String?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MeasurementsMessageState(
        title = message?.takeIf(String::isNotBlank)
            ?: stringResource(R.string.measurements_error),
        description = null,
        actionLabel = stringResource(R.string.measurements_retry),
        onActionClick = onRetryClick,
        modifier = modifier
    )
}

@Composable
fun MeasurementsEmptyState(
    onAddMeasurementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 62.dp, start = 24.dp, end = 24.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyMeasurementIllustration()
        Text(
            text = stringResource(R.string.measurements_empty_title),
            modifier = Modifier.padding(top = 24.dp),
            color = VitalTraceNavy,
            fontFamily = FontFamily.Serif,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.measurements_empty_description),
            modifier = Modifier.padding(top = 14.dp),
            color = Color(0xFF53636D),
            fontSize = 19.sp,
            lineHeight = 28.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onAddMeasurementClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VitalTraceTeal),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 17.dp)
        ) {
            Text(
                text = stringResource(R.string.measurements_empty_action),
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyMeasurementIllustration() {
    val strokeColor = VitalTraceMint
    Box(
        modifier = Modifier
            .size(100.dp)
            .drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    cornerRadius = CornerRadius(22.dp.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(7.dp.toPx(), 5.dp.toPx())
                        )
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.MonitorHeart,
            contentDescription = null,
            tint = VitalTraceMint,
            modifier = Modifier.size(44.dp)
        )
    }
}

@Composable
private fun MeasurementsMessageState(
    title: String,
    description: String?,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.MonitorHeart,
            contentDescription = null,
            tint = VitalTraceTeal
        )
        Text(
            text = title,
            color = VitalTraceNavy,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        description?.let {
            Text(
                text = it,
                color = Color(0xFF53636D),
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onActionClick,
            colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)
        ) {
            Text(text = actionLabel)
        }
    }
}
