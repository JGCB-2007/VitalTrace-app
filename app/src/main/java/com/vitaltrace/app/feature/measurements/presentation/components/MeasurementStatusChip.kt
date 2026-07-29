package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Icon
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
    modifier: Modifier = Modifier
) {
    val presentation = statusPresentation(status)
    Surface(
        modifier = modifier,
        color = presentation.background,
        contentColor = presentation.content,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = presentation.icon,
                contentDescription = null,
                modifier = Modifier.padding(1.dp)
            )
            Text(
                text = stringResource(presentation.label),
                fontSize = 15.sp,
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

private fun statusPresentation(status: MeasurementStatus): MeasurementStatusPresentation {
    return when (status) {
        MeasurementStatus.REGISTERED -> MeasurementStatusPresentation(
            label = R.string.measurements_status_registered,
            icon = Icons.Rounded.Upload,
            background = Color(0xFFE5EDF4),
            content = Color(0xFF315A76)
        )
        MeasurementStatus.PENDING -> MeasurementStatusPresentation(
            label = R.string.measurements_status_pending,
            icon = Icons.Rounded.Schedule,
            background = Color(0xFFFFEBC7),
            content = Color(0xFF765315)
        )
        MeasurementStatus.IN_REVIEW -> MeasurementStatusPresentation(
            label = R.string.measurements_status_in_review,
            icon = Icons.Rounded.Schedule,
            background = Color(0xFFD9EEEE),
            content = VitalTraceTeal
        )
        MeasurementStatus.REVIEWED -> MeasurementStatusPresentation(
            label = R.string.measurements_status_reviewed,
            icon = Icons.Rounded.Check,
            background = Color(0xFFDDF1E7),
            content = Color(0xFF23805F)
        )
    }
}
