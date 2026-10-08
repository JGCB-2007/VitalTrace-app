package com.vitaltrace.app.feature.profile.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Screenshot
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.core.presentation.components.ThemePreferenceSelector
import com.vitaltrace.app.core.settings.ThemePreference
import com.vitaltrace.app.feature.profile.presentation.AppSettingsUiModel

@Composable
fun AppearanceSettingsCard(
    settings: AppSettingsUiModel,
    onThemeChange: (ThemePreference) -> Unit,
    onSecureScreenChange: (Boolean) -> Unit,
    onLargeTextChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "APARIENCIA Y PRIVACIDAD",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ThemePreferenceSelector(
                    theme = settings.theme,
                    onThemeChange = onThemeChange,
                    showDescription = false
                )
                HorizontalDivider()
                PrivacyRow(
                    icon = Icons.Rounded.TextFields,
                    title = "Texto ampliado",
                    description = "Aumenta el tamaño del texto sin ignorar la configuración del sistema.",
                    checked = settings.largeTextEnabled,
                    onCheckedChange = onLargeTextChange
                )
                HorizontalDivider()
                PrivacyRow(
                    icon = Icons.Rounded.Screenshot,
                    title = "Bloquear capturas y grabación",
                    description = "Opcional. Al activarlo, las capturas y grabaciones se verán negras.",
                    checked = settings.secureScreenEnabled,
                    onCheckedChange = onSecureScreenChange
                )
            }
        }
    }
}

@Composable
private fun PrivacyRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary)
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
