package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.measurements.presentation.MeasurementStatus
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun MeasurementStatusChip(
    status: MeasurementStatus,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val presentation = statusPresentation(status)
    Surface(
        modifier = modifier,
        color = presentation.background,
        contentColor = presentation.content,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 10.dp else 14.dp,
                vertical = if (compact) 6.dp else 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = presentation.icon,
                contentDescription = null,
                modifier = Modifier.size(if (compact) 18.dp else 24.dp)
            )
            Text(
                text = stringResource(presentation.label),
                fontSize = if (compact) 13.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

private data class MeasurementStatusPresentation(
    @param:StringRes val label: Int,
    val icon: ImageVector,
    val background: Color,
    val content: Color
)

@Composable
private fun statusPresentation(status: MeasurementStatus): MeasurementStatusPresentation {
    return when (status) {
        MeasurementStatus.PENDING -> MeasurementStatusPresentation(
            label = R.string.measurements_status_pending,
            icon = Icons.Rounded.Schedule,
            background = MaterialTheme.colorScheme.secondaryContainer,
            content = MaterialTheme.colorScheme.onSecondaryContainer
        )
        MeasurementStatus.REVIEWED -> MeasurementStatusPresentation(
            label = R.string.measurements_status_reviewed,
            icon = Icons.Rounded.Check,
            background = MaterialTheme.colorScheme.tertiaryContainer,
            content = MaterialTheme.colorScheme.onTertiaryContainer
        )
        MeasurementStatus.UNKNOWN -> MeasurementStatusPresentation(
            label = R.string.measurements_status_unknown,
            icon = Icons.Rounded.Upload,
            background = MaterialTheme.colorScheme.surfaceVariant,
            content = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
