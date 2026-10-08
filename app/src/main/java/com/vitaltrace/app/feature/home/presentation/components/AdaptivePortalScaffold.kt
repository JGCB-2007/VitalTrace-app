package com.vitaltrace.app.feature.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination

@Composable
fun AdaptivePortalScaffold(
    selectedDestination: HomeBottomDestination,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val windowWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    val wide = windowWidth >= 840.dp
    val items = portalNavigationItems(onHomeClick, onMeasurementsClick, onAppointmentsClick, onProfileClick)
    Scaffold(
        modifier = modifier,
        containerColor = containerColor,
        bottomBar = {
            if (!wide) HomeBottomBar(
                selectedDestination,
                onHomeClick,
                onMeasurementsClick,
                onAppointmentsClick,
                onProfileClick
            )
        },
        floatingActionButton = floatingActionButton
    ) { padding ->
        Row(Modifier.fillMaxSize().padding(padding)) {
            if (wide) {
                NavigationRail(containerColor = MaterialTheme.colorScheme.surface) {
                    items.forEach { item ->
                        val label = stringResource(item.label)
                        NavigationRailItem(
                            selected = item.destination == selectedDestination,
                            onClick = item.onClick,
                            icon = { Icon(item.icon, label) },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            }
            Box(Modifier.weight(1f).fillMaxSize()) { content(PaddingValues(0.dp)) }
        }
    }
}

private data class PortalNavigationItem(
    val destination: HomeBottomDestination,
    val label: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)

private fun portalNavigationItems(
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit
) = listOf(
    PortalNavigationItem(HomeBottomDestination.HOME, R.string.home_bottom_home, Icons.Default.Home, onHomeClick),
    PortalNavigationItem(HomeBottomDestination.MEASUREMENTS, R.string.home_bottom_measurements, Icons.Default.Favorite, onMeasurementsClick),
    PortalNavigationItem(HomeBottomDestination.APPOINTMENTS, R.string.home_bottom_appointments, Icons.Default.CalendarMonth, onAppointmentsClick),
    PortalNavigationItem(HomeBottomDestination.PROFILE, R.string.home_bottom_profile, Icons.Default.Person, onProfileClick)
)
