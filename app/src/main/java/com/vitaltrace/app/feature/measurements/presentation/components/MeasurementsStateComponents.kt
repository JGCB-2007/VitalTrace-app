package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
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
    MeasurementsMessageState(
        title = stringResource(R.string.measurements_empty_title),
        description = stringResource(R.string.measurements_empty_description),
        actionLabel = stringResource(R.string.measurements_empty_action),
        onActionClick = onAddMeasurementClick,
        modifier = modifier
    )
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
