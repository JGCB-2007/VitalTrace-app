package com.vitaltrace.app.feature.timeline.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.MedicalInformation
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthTimelineScreen(
    onNavigateBack: () -> Unit,
    viewModel: HealthTimelineViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Línea de tiempo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null && state.events.isEmpty() -> Column(
                Modifier.fillMaxSize().padding(padding).padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = viewModel::retry, modifier = Modifier.padding(top = 16.dp)) { Text("Reintentar") }
            }
            else -> PullToRefreshBox(
                isRefreshing = state.refreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().widthIn(max = 760.dp).align(Alignment.TopCenter),
                    contentPadding = PaddingValues(24.dp, 16.dp, 24.dp, 40.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = state.query,
                            onValueChange = viewModel::updateQuery,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Rounded.Search, null) },
                            placeholder = { Text("Buscar eventos de salud") },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp)
                        )
                    }
                    item {
                        TimelineFilters(
                            selectedType = state.selectedType,
                            onTypeSelected = viewModel::selectType
                        )
                    }
                    if (state.visibleEvents.isEmpty()) {
                        item {
                            Text(
                                "No hay eventos que coincidan con estos filtros.",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(state.visibleEvents, key = TimelineEventUiModel::id) { event ->
                            TimelineEventCard(event)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineFilters(
    selectedType: TimelineEventType,
    onTypeSelected: (TimelineEventType) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val columns = if (maxWidth < 520.dp) 2 else 4
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TimelineEventType.entries.chunked(columns).forEach { rowTypes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowTypes.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { onTypeSelected(type) },
                            label = {
                                Text(
                                    text = type.label(),
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEventCard(event: TimelineEventUiModel) {
    val icon: ImageVector = when (event.type) {
        TimelineEventType.APPOINTMENT -> Icons.Rounded.CalendarMonth
        TimelineEventType.MEASUREMENT -> Icons.Rounded.MedicalInformation
        TimelineEventType.TREATMENT -> Icons.Rounded.Medication
        TimelineEventType.ALL -> Icons.Rounded.MedicalInformation
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(44.dp).background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = MaterialTheme.colorScheme.secondary) }
            Box(Modifier.width(2.dp).height(78.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)))
        }
        Card(
            Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(event.type.label().uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Text(event.status, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(event.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(event.date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun TimelineEventType.label() = when (this) {
    TimelineEventType.ALL -> "Todo"
    TimelineEventType.APPOINTMENT -> "Citas"
    TimelineEventType.MEASUREMENT -> "Mediciones"
    TimelineEventType.TREATMENT -> "Tratamientos"
}
