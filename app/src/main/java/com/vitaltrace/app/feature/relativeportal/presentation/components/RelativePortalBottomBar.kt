package com.vitaltrace.app.feature.relativeportal.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.relativeportal.presentation.RelativeSection
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
fun RelativePortalBottomBar(
    selected: RelativeSection,
    onSelect: (RelativeSection) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        relativeDestinations.forEach { destination ->
            NavigationBarItem(
                selected = selected == destination.section,
                onClick = { onSelect(destination.section) },
                icon = { Icon(destination.icon, destination.label) },
                label = {
                    Text(
                        text = destination.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Center
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VitalTraceNavy,
                    selectedTextColor = VitalTraceNavy,
                    indicatorColor = Color(0xFFD9F2F0),
                    unselectedIconColor = Color(0xFF9AA5AC),
                    unselectedTextColor = Color(0xFF9AA5AC)
                )
            )
        }
    }
}

private data class RelativeDestination(
    val section: RelativeSection,
    val label: String,
    val icon: ImageVector
)

private val relativeDestinations = listOf(
    RelativeDestination(RelativeSection.HOME, "Inicio", Icons.Rounded.Home),
    RelativeDestination(RelativeSection.APPOINTMENTS, "Citas", Icons.Rounded.CalendarMonth),
    RelativeDestination(RelativeSection.MEASUREMENTS, "Mediciones", Icons.Rounded.Favorite),
    RelativeDestination(RelativeSection.TREATMENTS, "Tratamientos", Icons.Rounded.Medication),
    RelativeDestination(RelativeSection.HISTORY, "Historial", Icons.Rounded.HistoryEdu)
)
