package com.vitaltrace.app.core.presentation.components

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
import com.vitaltrace.app.ui.theme.VitalTraceNavy

data class PortalBottomDestination<T>(val destination: T, val label: String, val icon: ImageVector)

@Composable
fun <T> VitalTracePortalBottomBar(
    selected: T,
    destinations: List<PortalBottomDestination<T>>,
    onSelect: (T) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        destinations.forEach { item ->
            NavigationBarItem(
                selected = selected == item.destination,
                onClick = { onSelect(item.destination) },
                icon = { Icon(item.icon, item.label) },
                label = {
                    Text(
                        text = item.label,
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