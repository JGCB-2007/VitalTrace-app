package com.vitaltrace.app.feature.relativeportal.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.runtime.Composable
import com.vitaltrace.app.core.presentation.components.PortalBottomDestination
import com.vitaltrace.app.core.presentation.components.VitalTracePortalBottomBar
import com.vitaltrace.app.feature.relativeportal.presentation.RelativeSection

@Composable
fun RelativePortalBottomBar(selected: RelativeSection, onSelect: (RelativeSection) -> Unit) {
    VitalTracePortalBottomBar(selected, relativeDestinations, onSelect)
}

private val relativeDestinations = listOf(
    PortalBottomDestination(RelativeSection.HOME, "Inicio", Icons.Rounded.Home),
    PortalBottomDestination(RelativeSection.APPOINTMENTS, "Citas", Icons.Rounded.CalendarMonth),
    PortalBottomDestination(RelativeSection.MEASUREMENTS, "Mediciones", Icons.Rounded.Favorite),
    PortalBottomDestination(RelativeSection.TREATMENTS, "Tratamientos", Icons.Rounded.Medication),
    PortalBottomDestination(RelativeSection.HISTORY, "Historial", Icons.Rounded.HistoryEdu)
)
