package com.vitaltrace.app.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting, color = Color(0xFF53636D), fontWeight = FontWeight.SemiBold)
                    Text(displayedName, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = VitalTraceTeal) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(initials(displayedName), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFE5E0D7))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Estás viendo a", color = VitalTraceTeal, fontWeight = FontWeight.Bold)
                    Text(patientName, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    patientRecordNumber?.takeIf(String::isNotBlank)?.let { Text(it, color = Color(0xFF53636D), style = MaterialTheme.typography.bodyMedium) }
                }
                if (canChange) TextButton(onClick = onChange) { Text("Cambiar", color = VitalTraceTeal, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

private fun initials(name: String): String = name.trim().split(Regex("\\s+"))
    .filter(String::isNotBlank).take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")