package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.feature.measurements.presentation.MeasurementPeriod
import com.vitaltrace.app.feature.measurements.presentation.MeasurementUiModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MeasurementInsightsCard(
    measurements: List<MeasurementUiModel>,
    availableTypes: List<String>,
    selectedType: String?,
    selectedPeriod: MeasurementPeriod,
    onTypeSelected: (String?) -> Unit,
    onPeriodSelected: (MeasurementPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("TENDENCIA Y RESUMEN", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.ExtraBold)
            if (availableTypes.isNotEmpty()) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableTypes.forEach { type ->
                        FilterChip(
                            selected = (selectedType ?: availableTypes.first()) == type,
                            onClick = { onTypeSelected(type) },
                            label = { Text(type) }
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MeasurementPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = selectedPeriod == period,
                        onClick = { onPeriodSelected(period) },
                        label = { Text(period.label()) }
                    )
                }
            }
            val values = measurements.mapNotNull(MeasurementUiModel::numericValue)
            if (values.isEmpty()) {
                Text("Registra más mediciones para ver tendencias.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Metric("Promedio", values.average().format())
                    Metric("Mínimo", values.min().format())
                    Metric("Máximo", values.max().format())
                }
                InteractiveTrendChart(measurements)
                Text(
                    "Las franjas resaltadas son referencias generales y no constituyen un diagnóstico.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String) = Column {
    Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun InteractiveTrendChart(measurements: List<MeasurementUiModel>) {
    val items = measurements.filter { it.numericValue != null }.takeLast(16)
    var selectedIndex by remember(items) { mutableIntStateOf(items.lastIndex.coerceAtLeast(0)) }
    val values = items.mapNotNull(MeasurementUiModel::numericValue)
    if (values.isEmpty()) return
    val type = items.first().typeName
    val range = healthyRange(type)
    val floor = minOf(values.min(), range?.first ?: values.min())
    val ceiling = maxOf(values.max(), range?.second ?: values.max())
    val span = (ceiling - floor).takeIf { it > 0.0 } ?: 1.0
    val reveal by animateFloatAsState(1f, tween(550), label = "chart-reveal")
    val lineColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val rangeColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f)
    val surfaceColor = MaterialTheme.colorScheme.surface

    val selected = items.getOrNull(selectedIndex)
    Text(
        selected?.let { "${it.date}: ${it.value} ${it.unit}" }.orEmpty(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.secondary,
        fontWeight = FontWeight.Bold
    )
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(190.dp)
            .pointerInput(items) {
                detectTapGestures { tap ->
                    if (items.size > 1) {
                        selectedIndex = ((tap.x / size.width) * (items.size - 1)).roundToInt()
                            .coerceIn(items.indices)
                    }
                }
            }
    ) {
        repeat(4) { index ->
            val y = size.height * index / 3f
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
        }
        range?.let { safe ->
            val top = size.height * (1f - ((safe.second - floor) / span).toFloat())
            val bottom = size.height * (1f - ((safe.first - floor) / span).toFloat())
            drawRect(rangeColor, Offset(0f, top), androidx.compose.ui.geometry.Size(size.width, bottom - top))
        }
        val path = Path()
        items.forEachIndexed { index, item ->
            val x = if (items.size == 1) size.width / 2 else size.width * index / (items.size - 1f)
            val y = size.height * (1f - (((item.numericValue ?: floor) - floor) / span).toFloat())
            val animatedY = size.height + (y - size.height) * reveal
            if (index == 0) path.moveTo(x, animatedY) else path.lineTo(x, animatedY)
        }
        drawPath(path, lineColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
        items.forEachIndexed { index, item ->
            val x = if (items.size == 1) size.width / 2 else size.width * index / (items.size - 1f)
            val y = size.height * (1f - (((item.numericValue ?: floor) - floor) / span).toFloat())
            val animatedY = size.height + (y - size.height) * reveal
            drawCircle(lineColor, if (index == selectedIndex) 10f else 7f, Offset(x, animatedY))
            drawCircle(surfaceColor, if (index == selectedIndex) 4f else 2.5f, Offset(x, animatedY))
        }
    }
}

private fun healthyRange(type: String): Pair<Double, Double>? = when {
    type.contains("presión", true) -> 90.0 to 140.0
    type.contains("glucosa", true) -> 70.0 to 180.0
    type.contains("oxígeno", true) -> 92.0 to 100.0
    else -> null
}

private fun Double.format(): String = String.format(Locale.getDefault(), "%.1f", this)

private fun MeasurementPeriod.label() = when (this) {
    MeasurementPeriod.DAYS_7 -> "7 d"
    MeasurementPeriod.DAYS_30 -> "30 d"
    MeasurementPeriod.DAYS_90 -> "3 m"
    MeasurementPeriod.ALL -> "Todo"
}
