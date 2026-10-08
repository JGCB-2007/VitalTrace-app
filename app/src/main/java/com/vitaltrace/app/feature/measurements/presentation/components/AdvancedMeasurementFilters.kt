package com.vitaltrace.app.feature.measurements.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdvancedMeasurementFilters(
    query: String,
    availableTypes: List<String>,
    selectedType: String?,
    attentionOnly: Boolean,
    onQueryChange: (String) -> Unit,
    onTypeSelected: (String?) -> Unit,
    onAttentionToggle: () -> Unit,
    onClear: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            placeholder = { Text("Buscar por tipo, valor u observación") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = selectedType == null, onClick = { onTypeSelected(null) }, label = { Text("Todos los tipos") })
            availableTypes.forEach { type ->
                FilterChip(selected = selectedType == type, onClick = { onTypeSelected(type) }, label = { Text(type) })
            }
            FilterChip(selected = attentionOnly, onClick = onAttentionToggle, label = { Text("Fuera de referencia") })
            if (query.isNotBlank() || selectedType != null || attentionOnly) {
                TextButton(onClick = onClear) { Text("Limpiar") }
            }
        }
    }
}
