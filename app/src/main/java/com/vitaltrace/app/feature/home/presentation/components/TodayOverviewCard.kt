package com.vitaltrace.app.feature.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.ui.theme.SoraFontFamily
import com.vitaltrace.app.feature.home.presentation.NextAppointmentUiModel
import com.vitaltrace.app.feature.home.presentation.RecentMeasurementUiModel

@Composable
fun TodayOverviewCard(
    appointment: NextAppointmentUiModel?,
    measurement: RecentMeasurementUiModel?,
    activeTreatmentsCount: Int,
    onRegisterMeasurement: () -> Unit,
    onAppointmentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "TU DÍA",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Lo importante, en un solo lugar",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = SoraFontFamily,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            TodayRow(
                icon = Icons.Rounded.CalendarMonth,
                label = "Próxima cita",
                value = appointment?.let { "${it.date} · ${it.time}" } ?: "Sin cita próxima",
                onClick = appointment?.let { onAppointmentClick }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TodayRow(
                icon = Icons.Rounded.MonitorHeart,
                label = "Última medición",
                value = measurement?.let { "${it.value} ${it.unit} · ${it.date}" }
                    ?: "Aún no hay mediciones"
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TodayRow(
                icon = Icons.Rounded.Medication,
                label = "Tratamientos",
                value = when (activeTreatmentsCount) {
                    0 -> "Sin tratamientos activos"
                    1 -> "1 tratamiento activo"
                    else -> "$activeTreatmentsCount tratamientos activos"
                }
            )
            Button(
                onClick = onRegisterMeasurement,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Rounded.MonitorHeart, null, Modifier.size(20.dp))
                Text(
                    "Registrar medición",
                    modifier = Modifier.padding(start = 9.dp, top = 5.dp, bottom = 5.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TodayRow(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.tertiaryContainer) {
            Icon(icon, null, Modifier.padding(10.dp).size(22.dp), tint = MaterialTheme.colorScheme.secondary)
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
        }
        onClick?.let {
            Surface(onClick = it, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Icon(Icons.Rounded.ChevronRight, "Ver detalle", Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}
