package com.vitaltrace.app.feature.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateContentSize
import com.vitaltrace.app.feature.home.presentation.HealthStatusLevel
import com.vitaltrace.app.feature.home.presentation.HealthStatusUiModel

@Composable
fun HealthOverviewCard(
    status: HealthStatusUiModel,
    activeTreatmentsCount: Int,
    onTimelineClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val treatmentsLabel = if (activeTreatmentsCount == 1) "tratamiento activo" else "tratamientos activos"
    val alertsLabel = if (status.openAlerts == 1) "alerta" else "alertas"
    val accent = when (status.level) {
        HealthStatusLevel.STABLE -> Color(0xFF21805F)
        HealthStatusLevel.ATTENTION -> Color(0xFF9A6500)
        HealthStatusLevel.CRITICAL -> MaterialTheme.colorScheme.error
    }
    val icon = when (status.level) {
        HealthStatusLevel.STABLE -> Icons.Rounded.CheckCircle
        HealthStatusLevel.ATTENTION -> Icons.Rounded.Warning
        HealthStatusLevel.CRITICAL -> Icons.Rounded.Error
    }
    Card(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = accent.copy(alpha = 0.14f)) {
                    Icon(icon, null, tint = accent, modifier = Modifier.padding(13.dp).size(30.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(status.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(status.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                "$activeTreatmentsCount $treatmentsLabel · ${status.openAlerts} $alertsLabel",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedButton(
                onClick = onTimelineClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Timeline, null, modifier = Modifier.size(20.dp))
                Text(
                    "Ver actividad clínica",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
