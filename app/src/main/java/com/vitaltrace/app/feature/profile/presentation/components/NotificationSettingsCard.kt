package com.vitaltrace.app.feature.profile.presentation.components

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
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.profile.presentation.NotificationSettingsUiModel
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun NotificationSettingsCard(
    settings: NotificationSettingsUiModel,
    onMeasurementRemindersChange: (Boolean) -> Unit,
    onAppointmentNotificationsChange: (Boolean) -> Unit,
    onEmailUpdatesChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.profile_notifications),
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
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                NotificationSettingRow(
                    label = stringResource(R.string.profile_measurement_reminders),
                    icon = Icons.Rounded.NotificationsNone,
                    checked = settings.measurementRemindersEnabled,
                    onCheckedChange = onMeasurementRemindersChange
                )
                HorizontalDivider(color = Color(0xFFE5E0D7))
                NotificationSettingRow(
                    label = stringResource(R.string.profile_appointment_notifications),
                    icon = Icons.Rounded.CalendarMonth,
                    checked = settings.appointmentNotificationsEnabled,
                    onCheckedChange = onAppointmentNotificationsChange
                )
                HorizontalDivider(color = Color(0xFFE5E0D7))
                NotificationSettingRow(
                    label = stringResource(R.string.profile_email_updates),
                    icon = Icons.Rounded.MailOutline,
                    checked = settings.emailUpdatesEnabled,
                    onCheckedChange = onEmailUpdatesChange
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
            color = Color(0xFFDDF4F2),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VitalTraceTeal,
                modifier = Modifier.padding(15.dp)
            )
        }
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            color = Color(0xFF172C3A),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 21.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VitalTraceTeal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD2CCBF),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
