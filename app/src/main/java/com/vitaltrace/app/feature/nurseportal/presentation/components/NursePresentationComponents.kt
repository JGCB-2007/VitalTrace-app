package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.ui.theme.*

@Composable
internal fun NurseStatusChip(text: String, background: Color, content: Color) {
    Surface(color = background, contentColor = content, shape = RoundedCornerShape(50)) {
        Text(text, Modifier.padding(horizontal = 12.dp, vertical = 7.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
internal fun NurseSectionHeader(icon: ImageVector, title: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = VitalTraceTeal)
        Text(title, color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 23.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun NurseDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF53636D))
        Text(value, Modifier.weight(1f).padding(start = 18.dp), color = VitalTraceNavy, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun NurseInlineEmpty(icon: ImageVector, message: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(Color.White), elevation = CardDefaults.cardElevation(5.dp)) {
        Row(Modifier.padding(22.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = VitalTraceMint)
            Text(message, color = Color(0xFF53636D))
        }
    }
}

@Composable
internal fun NurseCenteredEmpty(icon: ImageVector, title: String, description: String, modifier: Modifier) {
    Column(modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(icon, null, Modifier.size(72.dp), tint = VitalTraceMint)
        Text(title, Modifier.padding(top = 22.dp), color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(description, Modifier.padding(top = 12.dp), color = Color(0xFF53636D), textAlign = TextAlign.Center)
    }
}