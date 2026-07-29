package com.vitaltrace.app.feature.home.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.home.presentation.HomeBottomDestination

@Composable
fun HomeBottomBar(
    selectedDestination: HomeBottomDestination,
    onHomeClick: () -> Unit,
    onMeasurementsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val items = listOf(
        HomeBottomItem(
            destination = HomeBottomDestination.HOME,
            label = R.string.home_bottom_home,
            icon = Icons.Default.Home,
            onClick = onHomeClick
        ),
        HomeBottomItem(
            destination = HomeBottomDestination.MEASUREMENTS,
            label = R.string.home_bottom_measurements,
            icon = Icons.Default.Favorite,
            onClick = onMeasurementsClick
        ),
        HomeBottomItem(
            destination = HomeBottomDestination.APPOINTMENTS,
            label = R.string.home_bottom_appointments,
            icon = Icons.Default.CalendarMonth,
            onClick = onAppointmentsClick
        ),
        HomeBottomItem(
            destination = HomeBottomDestination.PROFILE,
            label = R.string.home_bottom_profile,
            icon = Icons.Default.Person,
            onClick = onProfileClick
        )
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { item ->
            val label = stringResource(item.label)
            NavigationBarItem(
                selected = selectedDestination == item.destination,
                onClick = item.onClick,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(text = label, fontWeight = FontWeight.SemiBold)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = HomeNavy,
                    selectedTextColor = HomeNavy,
                    indicatorColor = HomeMintContainer,
                    unselectedIconColor = HomeInactive,
                    unselectedTextColor = HomeInactive
                )
            )
        }
    }
}

private data class HomeBottomItem(
    val destination: HomeBottomDestination,
    @param:StringRes val label: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
)
