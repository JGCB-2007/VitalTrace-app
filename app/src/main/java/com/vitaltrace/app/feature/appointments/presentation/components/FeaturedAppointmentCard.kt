package com.vitaltrace.app.feature.appointments.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.appointments.presentation.AppointmentUiModel
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun FeaturedAppointmentCard(
    appointment: AppointmentUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = appointmentStatusLabel(appointment.status)
    val description = stringResource(
        R.string.appointments_item_description,
        appointment.professionalName,
        appointment.reason,
        "${appointment.date}, ${appointment.time}",
        status
    )
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = VitalTraceTeal),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.appointments_next),
                    color = VitalTraceMint,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                AppointmentStatusChip(
                    status = appointment.status,
                    prominent = true
                )
            }
            Text(
                text = appointment.professionalName,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = appointment.reason,
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 18.sp
            )
            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                AppointmentMetadata(
                    icon = Icons.Rounded.CalendarMonth,
                    text = appointment.date
                )
                AppointmentMetadata(
                    icon = Icons.Rounded.Schedule,
                    text = appointment.time
                )
            }
        }
    }
}

@Composable
private fun AppointmentMetadata(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        Text(text = text, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}
