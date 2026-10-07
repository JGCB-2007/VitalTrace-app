package com.vitaltrace.app.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ObservedPatientHeader(
    greeting: String,
    authenticatedName: String,
    patientName: String,
    patientRecordNumber: String? = null,
    canChange: Boolean,
    onChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedName = authenticatedName.ifBlank { "Usuario" }
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    Text(displayedName, color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Serif, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondary) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(initials(displayedName), color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Estás viendo a", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Text(patientName, color = MaterialTheme.colorScheme.onSurface, fontFamily = FontFamily.Serif, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    patientRecordNumber?.takeIf(String::isNotBlank)?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium) }
                }
                if (canChange) TextButton(onClick = onChange) { Text("Cambiar", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

private fun initials(name: String): String = name.trim().split(Regex("\\s+"))
    .filter(String::isNotBlank).take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
