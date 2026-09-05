package com.vitaltrace.app.feature.measurements.presentation.form.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.core.presentation.localization.SpanishDateTime
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun MeasurementDateTimeFields(
    date: LocalDate,
    time: LocalTime,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateText = SpanishDateTime.formatDate(date)
    val timeText = SpanishDateTime.formatTime(time)
    val context = LocalContext.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        DateTimeField(
            label = stringResource(R.string.measurement_form_date),
            value = dateText,
            description = stringResource(R.string.measurement_form_date_description, dateText),
            icon = Icons.Rounded.CalendarMonth,
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        onDateChange(LocalDate.of(year, month + 1, day))
                    },
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth
                ).show()
            },
            modifier = Modifier.weight(1f)
        )
        DateTimeField(
            label = stringResource(R.string.measurement_form_time),
            value = timeText,
            description = stringResource(R.string.measurement_form_time_description, timeText),
            icon = Icons.Rounded.Schedule,
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute -> onTimeChange(LocalTime.of(hour, minute)) },
                    time.hour,
                    time.minute,
                    false
                ).show()
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DateTimeField(
    label: String,
    value: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FieldLabel(text = label)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = description }
                .clickable(role = Role.Button, onClick = onClick),
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1DDD3))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF657078))
                Text(
                    text = value,
                    color = VitalTraceNavy,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    maxLines = 1
                )
            }
        }
    }
}
