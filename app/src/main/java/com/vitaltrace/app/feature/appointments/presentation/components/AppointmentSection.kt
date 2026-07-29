package com.vitaltrace.app.feature.appointments.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.appointments.presentation.AppointmentStatus
import com.vitaltrace.app.feature.appointments.presentation.AppointmentUiModel
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun AppointmentSection(
    title: String,
    appointments: List<AppointmentUiModel>,
    emptyMessage: String,
    onAppointmentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            color = VitalTraceTeal,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            if (appointments.isEmpty()) {
                Text(
                    text = emptyMessage,
                    modifier = Modifier.padding(24.dp),
                    color = Color(0xFF53636D)
                )
            } else {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)) {
                    appointments.forEachIndexed { index, appointment ->
                        AppointmentListItem(
                            appointment = appointment,
                            onClick = { onAppointmentClick(appointment.id) },
                            modifier = Modifier.padding(vertical = 18.dp)
                        )
                        if (index < appointments.lastIndex) {
                            HorizontalDivider(color = Color(0xFFE5E0D7))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentListItem(
    appointment: AppointmentUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusLabel = stringResource(
        if (appointment.status == AppointmentStatus.SCHEDULED) {
            R.string.appointments_status_scheduled
        } else {
            R.string.appointments_status_completed
        }
    )
    val description = stringResource(
        R.string.appointments_item_description,
        appointment.professionalName,
        appointment.reason,
        "${appointment.date}, ${appointment.time}",
        statusLabel
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            AppointmentLeadingIcon(status = appointment.status)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = appointment.professionalName,
                    color = VitalTraceNavy,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp,
                    maxLines = 2
                )
                Text(
                    text = appointment.reason,
                    color = Color(0xFF53636D),
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                AppointmentDateTime(
                    icon = Icons.Rounded.CalendarMonth,
                    text = appointment.date
                )
                AppointmentDateTime(
                    icon = Icons.Rounded.Schedule,
                    text = appointment.time
                )
            }
            AppointmentStatusChip(status = appointment.status)
        }
    }
}

@Composable
private fun AppointmentLeadingIcon(status: AppointmentStatus) {
    Surface(
        modifier = Modifier.size(60.dp),
        color = if (status == AppointmentStatus.SCHEDULED) {
            Color(0xFFDDF4F2)
        } else {
            Color(0xFFDDF1E7)
        },
        shape = RoundedCornerShape(18.dp)
    ) {
        Icon(
            imageVector = if (status == AppointmentStatus.SCHEDULED) {
                Icons.Rounded.CalendarMonth
            } else {
                Icons.Rounded.Check
            },
            contentDescription = null,
            tint = if (status == AppointmentStatus.SCHEDULED) {
                VitalTraceTeal
            } else {
                Color(0xFF23805F)
            },
            modifier = Modifier.padding(15.dp)
        )
    }
}

@Composable
private fun AppointmentDateTime(
    icon: ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF657078),
            modifier = Modifier.size(19.dp)
        )
        Text(
            text = text,
            color = Color(0xFF53636D),
            fontSize = 16.sp
        )
    }
}
