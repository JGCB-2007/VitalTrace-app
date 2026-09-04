package com.vitaltrace.app.feature.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R

@Composable
fun HomeHeader(
    greeting: String,
    patientName: String,
    patientInitials: String,
    isLoggingOut: Boolean,
    onLogoutClick: () -> Unit,
    unreadNotificationsCount: Int,
    onNotificationsClick: () -> Unit,
    subtitle: String? = null,
    showNotifications: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    val accountOptionsDescription = stringResource(R.string.home_account_options)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = greeting,
                color = HomeSupportingText,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = patientName,
                color = HomeNavy,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 2.dp)
            )
            subtitle?.takeIf(String::isNotBlank)?.let {
                Text(
                    text = it,
                    color = HomeSupportingText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        if (showNotifications) {
            BadgedBox(
                badge = {
                    if (unreadNotificationsCount > 0) {
                        Badge {
                            Text(if (unreadNotificationsCount > 99) "99+" else unreadNotificationsCount.toString())
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificaciones",
                        tint = HomeNavy
                    )
                }
            }
        }

        Box(modifier = Modifier.padding(start = 8.dp)) {
            Surface(
                onClick = { isMenuExpanded = true },
                modifier = Modifier
                    .size(60.dp)
                    .semantics {
                        contentDescription = accountOptionsDescription
                    },
                shape = RoundedCornerShape(20.dp),
                color = HomeTeal,
                enabled = !isLoggingOut
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = patientInitials,
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                shape = RoundedCornerShape(18.dp),
                containerColor = Color.White,
                shadowElevation = 8.dp
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.home_logout),
                            color = HomeNavy,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = null,
                            tint = HomeTeal
                        )
                    },
                    onClick = {
                        isMenuExpanded = false
                        showLogoutConfirmation = true
                    }
                )
            }
        }
    }

    if (showLogoutConfirmation) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutConfirmation = false
                onLogoutClick()
            },
            onDismiss = { showLogoutConfirmation = false }
        )
    }
}

