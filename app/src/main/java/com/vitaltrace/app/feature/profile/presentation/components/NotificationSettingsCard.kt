package com.vitaltrace.app.feature.profile.presentation.components

import android.app.TimePickerDialog
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
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.profile.presentation.NotificationSettingsUiModel
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotificationSettingsCard(
    settings: NotificationSettingsUiModel,
    onMeasurementRemindersChange: (Boolean) -> Unit,
    onAppointmentNotificationsChange: (Boolean) -> Unit,
    onReminderTimeChange: (Int, Int) -> Unit,
    onReminderDayToggle: (Int) -> Unit,
    onSnoozeMinutesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.profile_notifications),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                NotificationSettingRow(
                    label = stringResource(R.string.profile_measurement_reminders),
                    icon = Icons.Rounded.NotificationsNone,
                    checked = settings.measurementRemindersEnabled,
                    onCheckedChange = onMeasurementRemindersChange
                )
                if (settings.measurementRemindersEnabled) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ReminderScheduleEditor(
                        settings = settings,
                        onReminderTimeChange = onReminderTimeChange,
                        onReminderDayToggle = onReminderDayToggle,
                        onSnoozeMinutesChange = onSnoozeMinutesChange
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                NotificationSettingRow(
                    label = stringResource(R.string.profile_appointment_notifications),
                    icon = Icons.Rounded.CalendarMonth,
                    checked = settings.appointmentNotificationsEnabled,
                    onCheckedChange = onAppointmentNotificationsChange
                )
            }
        }
        Text(
            text = stringResource(R.string.profile_notifications_local_note),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun ReminderScheduleEditor(
    settings: NotificationSettingsUiModel,
    onReminderTimeChange: (Int, Int) -> Unit,
    onReminderDayToggle: (Int) -> Unit,
    onSnoozeMinutesChange: (Int) -> Unit
) {
    val context = LocalContext.current
    val time = String.format(Locale.getDefault(), "%02d:%02d", settings.reminderHour, settings.reminderMinute)
    Column(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> onReminderTimeChange(hour, minute) },
                        settings.reminderHour,
                        settings.reminderMinute,
                        true
                    ).show()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Column(Modifier.weight(1f)) {
                Text("Hora del recordatorio", fontWeight = FontWeight.Bold)
                Text(time, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Cambiar", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        }
        Text("Días", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEachIndexed { index, label ->
                FilterChip(
                    selected = index + 1 in settings.reminderDays,
                    onClick = { onReminderDayToggle(index + 1) },
                    label = { Text(label) }
                )
            }
        }
        Text("Posponer", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(15, 30, 60).forEach { minutes ->
                FilterChip(
                    selected = settings.snoozeMinutes == minutes,
                    onClick = { onSnoozeMinutesChange(minutes) },
                    label = { Text("$minutes min") }
                )
            }
        }
        if (settings.reminderHistory.isNotEmpty()) {
            Text("Actividad reciente", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            settings.reminderHistory.takeLast(3).reversed().forEach { entry ->
                val formatted = Instant.ofEpochMilli(entry.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.getDefault()))
                val status = when (entry.event) {
                    "COMPLETED" -> "Medición completada"
                    "SNOOZED", "SNOOZED_SENT" -> "Recordatorio pospuesto"
                    "MISSED" -> "Recordatorio omitido"
                    else -> "Recordatorio enviado"
                }
                Text(
                    "$status · $formatted",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun NotificationSettingRow(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(15.dp)
            )
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 21.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
