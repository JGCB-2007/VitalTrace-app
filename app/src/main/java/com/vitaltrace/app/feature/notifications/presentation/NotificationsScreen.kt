package com.vitaltrace.app.feature.notifications.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNotificationAction: (String?, Long?) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        color = VitalTraceNavy,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver", tint = VitalTraceNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VitalTraceWarmBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            NotificationToolbar(
                unreadCount = state.unreadCount,
                isMarkingAllAsRead = state.isMarkingAllAsRead,
                onMarkAllAsRead = viewModel::markAllAsRead
            )
            NotificationFilters(
                selected = state.selectedFilter,
                onSelected = viewModel::selectFilter
            )
            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (val content = state.contentState) {
                    NotificationsContentState.Loading -> NotificationsLoadingState()
                    NotificationsContentState.Empty -> NotificationsEmptyState()
                    is NotificationsContentState.Error -> NotificationsErrorState(
                        message = content.message,
                        onRetry = viewModel::retry
                    )
                    is NotificationsContentState.Success -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(content.notifications, key = PatientNotification::id) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = {
                                    viewModel.markAsRead(notification)
                                    onNotificationAction(notification.actionRoute, notification.relatedId)
                                }
                            )
                        }
                        if (content.currentPage < content.lastPage) {
                            item(key = "load-more") {
                                LaunchedEffect(content.currentPage, state.selectedFilter) {
                                    viewModel.loadMore()
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = VitalTraceTeal,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationToolbar(
    unreadCount: Int,
    isMarkingAllAsRead: Boolean,
    onMarkAllAsRead: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (unreadCount == 1) "1 sin leer" else "$unreadCount sin leer",
            color = VitalTraceNavy,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        TextButton(
            onClick = onMarkAllAsRead,
            enabled = unreadCount > 0 && !isMarkingAllAsRead
        ) {
            if (isMarkingAllAsRead) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = VitalTraceTeal
                )
                Spacer(Modifier.size(8.dp))
            }
            Text("Marcar todas como leÃ­das")
        }
    }
}

@Composable
private fun NotificationFilters(
    selected: NotificationFilter,
    onSelected: (NotificationFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotificationFilter.entries.forEach { filter ->
            val label = when (filter) {
                NotificationFilter.ALL -> "Todas"
                NotificationFilter.UNREAD -> "No leÃ­das"
                NotificationFilter.READ -> "LeÃ­das"
            }
            FilterChip(
                selected = selected == filter,
                onClick = { onSelected(filter) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun NotificationCard(
    notification: PatientNotification,
    onClick: () -> Unit
) {
    val container = if (notification.isRead) Color.White else VitalTraceTeal.copy(alpha = 0.08f)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = VitalTraceTeal.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = notificationIcon(notification),
                        contentDescription = null,
                        tint = VitalTraceTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 14.dp)
            ) {
                Text(
                    text = notification.title.orEmpty(),
                    color = VitalTraceNavy,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!notification.message.isNullOrBlank()) {
                    Text(
                        text = notification.message,
                        modifier = Modifier.padding(top = 5.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val date = NotificationDateFormatter.format(notification.createdAt)
                if (date.isNotBlank()) {
                    Text(
                        text = date,
                        modifier = Modifier.padding(top = 10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            if (!notification.isRead) {
                Surface(
                    modifier = Modifier.size(9.dp),
                    shape = CircleShape,
                    color = VitalTraceTeal
                ) {}
            }
        }
    }
}

private fun notificationIcon(notification: PatientNotification): ImageVector =
    when (notification.relatedType?.uppercase()) {
        "APPOINTMENT" -> Icons.Outlined.CalendarMonth
        "MEASUREMENT" -> Icons.Outlined.Favorite
        "TREATMENT" -> Icons.Outlined.Medication
        else -> Icons.Outlined.Notifications
    }

@Composable
private fun NotificationsLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = VitalTraceTeal)
    }
}

@Composable
private fun NotificationsEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = VitalTraceTeal.copy(alpha = 0.55f)
        )
        Text(
            text = "No tienes notificaciones.",
            modifier = Modifier.padding(top = 16.dp),
            color = VitalTraceNavy,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NotificationsErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.Notifications,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = VitalTraceTeal
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 16.dp),
            color = VitalTraceNavy,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 18.dp)) {
            Text("Reintentar")
        }
    }
}

