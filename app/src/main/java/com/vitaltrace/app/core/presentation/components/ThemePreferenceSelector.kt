package com.vitaltrace.app.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.core.settings.ThemePreference

@Composable
fun ThemePreferenceSelector(
    theme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
    modifier: Modifier = Modifier,
    showDescription: Boolean = true
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DarkMode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Column {
                Text("Tema", fontWeight = FontWeight.Bold)
                if (showDescription) {
                    Text(
                        "El cambio se aplica a todos los módulos y perfiles.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            themeOptions.forEach { (value, label) ->
                FilterChip(
                    selected = theme == value,
                    onClick = { onThemeChange(value) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
fun ThemePreferenceCard(
    theme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ThemePreferenceSelector(
            theme = theme,
            onThemeChange = onThemeChange,
            modifier = Modifier.padding(20.dp)
        )
    }
}

private val themeOptions = listOf(
    ThemePreference.SYSTEM to "Sistema",
    ThemePreference.LIGHT to "Claro",
    ThemePreference.DARK to "Oscuro"
)
