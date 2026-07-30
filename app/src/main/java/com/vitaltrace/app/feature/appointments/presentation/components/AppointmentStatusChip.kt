package com.vitaltrace.app.feature.appointments.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
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
import com.vitaltrace.app.feature.appointments.presentation.AppointmentStatus
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun AppointmentStatusChip(
    status: AppointmentStatus,
    modifier: Modifier = Modifier,
    prominent: Boolean = false
) {
    val presentation = status.presentation(prominent)
    Surface(
        modifier = modifier,
        color = presentation.background,
        contentColor = presentation.content,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!prominent) {
                Icon(
                    imageVector = presentation.icon,
                    contentDescription = null
                )
            }
            Text(
                text = stringResource(presentation.label),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun appointmentStatusLabel(status: AppointmentStatus): String {
    return stringResource(status.presentation(prominent = false).label)
}

private data class AppointmentStatusPresentation(
    @param:StringRes val label: Int,
    val icon: ImageVector,
    val background: Color,
    val content: Color
)

private fun AppointmentStatus.presentation(prominent: Boolean): AppointmentStatusPresentation {
    return when (this) {
        AppointmentStatus.SCHEDULED -> AppointmentStatusPresentation(
            label = R.string.appointments_status_scheduled,
            icon = Icons.Rounded.Schedule,
            background = if (prominent) Color.White.copy(alpha = 0.20f) else Color(0xFFDDF4F2),
            content = if (prominent) Color.White else VitalTraceTeal
        )
        AppointmentStatus.CONFIRMED -> AppointmentStatusPresentation(
            label = R.string.appointments_status_confirmed,
            icon = Icons.Rounded.Schedule,
            background = if (prominent) Color.White.copy(alpha = 0.20f) else Color(0xFFDDF4F2),
            content = if (prominent) Color.White else VitalTraceTeal
        )
        AppointmentStatus.ATTENDED -> AppointmentStatusPresentation(
            label = R.string.appointments_status_attended,
            icon = Icons.Rounded.Check,
            background = Color(0xFFDDF1E7),
            content = Color(0xFF23805F)
        )
        AppointmentStatus.CANCELLED -> AppointmentStatusPresentation(
            label = R.string.appointments_status_cancelled,
            icon = Icons.Rounded.Check,
            background = Color(0xFFF3E4E1),
            content = Color(0xFF8C3D32)
        )
        AppointmentStatus.NO_SHOW -> AppointmentStatusPresentation(
            label = R.string.appointments_status_no_show,
            icon = Icons.Rounded.Check,
            background = Color(0xFFF3E4E1),
            content = Color(0xFF8C3D32)
        )
        AppointmentStatus.UNKNOWN -> AppointmentStatusPresentation(
            label = R.string.appointments_status_unknown,
            icon = Icons.Rounded.Schedule,
            background = Color(0xFFE8E8E8),
            content = Color(0xFF53636D)
        )
    }
}
