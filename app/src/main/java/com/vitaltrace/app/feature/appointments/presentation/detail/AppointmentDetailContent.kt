package com.vitaltrace.app.feature.appointments.presentation.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.vitaltrace.app.feature.appointments.presentation.AppointmentDetailUiModel
import com.vitaltrace.app.feature.appointments.presentation.AppointmentStatus
import com.vitaltrace.app.feature.appointments.presentation.components.AppointmentStatusChip
import com.vitaltrace.app.feature.appointments.presentation.components.appointmentStatusLabel
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AppointmentDetailContent(
    detail: AppointmentDetailUiModel,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                text = stringResource(R.string.appointment_detail_title),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.7.sp
            )
            AppointmentStatusChip(status = detail.status)
        }
        AppointmentProfessional(detail = detail)
        AppointmentInformationCard(detail = detail)
        if (detail.scheduledAt.isNotBlank()) {
            OutlinedButton(
                onClick = { openInCalendar(context, detail) },
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
                Text(
                    text = stringResource(R.string.appointment_detail_add_calendar),
                    modifier = Modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        RescheduleNotice()
        OutlinedButton(
            onClick = onCloseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.appointment_detail_close),
                fontFamily = FontFamily.Serif,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun openInCalendar(context: Context, detail: AppointmentDetailUiModel) {
    val start = runCatching {
        LocalDateTime.parse(
            detail.scheduledAt.trim(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }.getOrNull() ?: return
    val end = start + detail.durationMinutes.coerceAtLeast(30) * 60_000L
    val intent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI).apply {
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
        putExtra(CalendarContract.Events.TITLE, context.getString(R.string.appointment_calendar_title))
        putExtra(CalendarContract.Events.DESCRIPTION, detail.reason)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // The calendar action is optional when the device has no compatible app.
    }
}

@Composable
fun AppointmentDetailDragHandle() {
    Box(
        modifier = Modifier
            .padding(top = 17.dp, bottom = 20.dp)
            .width(64.dp)
            .height(7.dp)
    ) {
        Surface(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.outlineVariant
        ) {}
    }
}

@Composable
private fun AppointmentProfessional(detail: AppointmentDetailUiModel) {
    val description = stringResource(
        R.string.appointment_detail_professional_description,
        detail.professionalName,
        detail.specialty
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(78.dp),
            color = Color(0xFF03657E),
            contentColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = detail.professionalInitials,
                modifier = Modifier.padding(top = 22.dp),
                fontFamily = FontFamily.Serif,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.professionalName,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Serif,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )
            Text(
                text = detail.specialty,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun AppointmentInformationCard(detail: AppointmentDetailUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            detail.contextName?.takeIf(String::isNotBlank)?.let {
                AppointmentDetailRow(label = "Paciente", value = it)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            AppointmentDetailRow(
                label = stringResource(R.string.appointment_detail_reason),
                value = detail.reason
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            AppointmentDetailRow(
                label = stringResource(R.string.appointment_detail_date),
                value = detail.date
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            AppointmentDetailRow(
                label = stringResource(R.string.appointment_detail_time),
                value = detail.time
            )
            if (detail.durationMinutes > 0) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                AppointmentDetailRow(
                    label = "Duración",
                    value = detail.durationMinutes.toString() + " min"
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            AppointmentDetailRow(
                label = stringResource(R.string.appointment_detail_status),
                value = appointmentStatusLabel(detail.status),
                valueColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun AppointmentDetailRow(
    label: String,
    value: String,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 18.sp)
        Text(
            text = value,
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp),
            color = valueColor ?: MaterialTheme.colorScheme.onSurface,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun RescheduleNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 19.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(imageVector = Icons.Rounded.Info, contentDescription = null)
            Text(
                text = stringResource(R.string.appointment_detail_reschedule_notice),
                fontSize = 16.sp,
                lineHeight = 23.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppointmentDetailPreview() {
    VitalTraceTheme(dynamicColor = false) {
        Surface(color = VitalTraceWarmBackground) {
            AppointmentDetailContent(
                detail = AppointmentDetailUiModel(
                    id = 1,
                    professionalName = "Dr. Carlos Ruiz",
                    professionalInitials = "CR",
                    specialty = "Medicina interna",
                    reason = "Control de presión",
                    date = "23 jul 2026",
                    time = "10:30 a. m.",
                    status = AppointmentStatus.SCHEDULED
                ),
                onCloseClick = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
