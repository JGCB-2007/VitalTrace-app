package com.vitaltrace.app.feature.notifications.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit,
    onNotificationAction: (String?, Long?) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val feedbackText = state.feedbackMessage?.let { message ->
        NotificationPresentationMapper.feedbackResource(message)?.let { stringResource(it) }
            ?: message
    }

    LaunchedEffect(feedbackText) {
        feedbackText?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        containerColor = VitalTraceWarmBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            NotificationsHeader(
                unreadCount = state.unreadCount,
                isMarkingAllAsRead = state.isMarkingAllAsRead,
                onNavigateBack = onNavigateBack,
                onMarkAllAsRead = viewModel::markAllAsRead
            )
            NotificationFilters(
                selected = state.selectedFilter,
                onSelected = viewModel::selectFilter,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            Crossfade(
                targetState = state.contentState,
                modifier = Modifier.fillMaxSize(),
                animationSpec = tween(220),
                label = "notification-content"
            ) { content ->
                when (content) {
                    NotificationsContentState.Loading -> NotificationsSkeletonList()
                    NotificationsContentState.Empty -> NotificationsEmptyState(onRefresh = viewModel::retry)
                    is NotificationsContentState.Error -> NotificationsErrorState(onRetry = viewModel::retry)
                    is NotificationsContentState.Success -> NotificationsList(
                        content = content,
                        isLoadingMore = state.isLoadingMore,
                        onLoadMore = viewModel::loadMore,
                        onNotificationClick = { notification ->
                            viewModel.markAsRead(notification)
                            onNotificationAction(notification.actionRoute, notification.relatedId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsHeader(
    unreadCount: Int,
    isMarkingAllAsRead: Boolean,
    onNavigateBack: () -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 4.dp, end = 12.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.notifications_back),
                tint = VitalTraceNavy
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.notifications_screen_title),
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (unreadCount == 1) stringResource(R.string.notifications_unread_one) else stringResource(R.string.notifications_unread_count, unreadCount),
                color = VitalTraceTeal,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium
            )
        }
        TextButton(
            onClick = onMarkAllAsRead,
            enabled = unreadCount > 0 && !isMarkingAllAsRead,
            modifier = Modifier
                .alpha(if (unreadCount > 0) 1f else 0.68f)
                .height(48.dp)
        ) {
            AnimatedVisibility(
                visible = isMarkingAllAsRead,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = VitalTraceTeal
                )
            }
            if (isMarkingAllAsRead) Spacer(Modifier.size(7.dp))
            Text(
                text = stringResource(R.string.notifications_mark_all),
                color = if (unreadCount > 0) VitalTraceTeal else VitalTraceNavy.copy(alpha = 0.62f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun NotificationFilters(
    selected: NotificationFilter,
    onSelected: (NotificationFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        NotificationFilter.entries.forEach { filter ->
            val label = when (filter) {
                NotificationFilter.ALL -> stringResource(R.string.notifications_filter_all)
                NotificationFilter.UNREAD -> stringResource(R.string.notifications_filter_unread)
                NotificationFilter.READ -> stringResource(R.string.notifications_filter_read)
            }
            FilterChip(
                selected = selected == filter,
                onClick = { onSelected(filter) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = VitalTraceNavy.copy(alpha = 0.82f),
                    selectedContainerColor = VitalTraceTeal,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == filter,
                    borderColor = VitalTraceTeal.copy(alpha = 0.65f),
                    selectedBorderColor = VitalTraceTeal
                )
            )
        }
    }
}

@Composable
private fun NotificationsList(
    content: NotificationsContentState.Success,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onNotificationClick: (PatientNotification) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(content.notifications, key = PatientNotification::id) { notification ->
            NotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                modifier = Modifier.animateItem()
            )
        }
        if (content.currentPage < content.lastPage) {
            item(key = "load-more") {
                LaunchedEffect(content.currentPage) {
                    onLoadMore()
                }
                AnimatedVisibility(visible = isLoadingMore) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = VitalTraceTeal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: PatientNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = notificationCategory(notification)
    val accent = categoryColor(category)
    val background = if (notification.isRead) Color.White else accent.copy(alpha = 0.055f)
    val presentation = NotificationPresentationMapper.map(notification)
    val visibleTitle = presentation.titleResource?.let { stringResource(it) }
        ?: notification.title.orEmpty()
    val visibleMessage = presentation.messageResource?.let { stringResource(it) }
        ?: notification.message.orEmpty()

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(220)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 1.dp else 3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            AnimatedVisibility(visible = !notification.isRead) {
                Box(
                    modifier = Modifier
                        .background(accent)
                        .width(4.dp)
                        .fillMaxHeight()
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.dp, vertical = 13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NotificationIcon(category = category, color = accent)
                    Text(
                        text = visibleTitle,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp),
                        color = VitalTraceNavy,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = NotificationDateFormatter.format(notification.createdAt),
                        color = VitalTraceNavy.copy(alpha = 0.64f),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.End
                    )
                }
                if (visibleMessage.isNotBlank()) {
                    Text(
                        text = visibleMessage,
                        modifier = Modifier.padding(top = 8.dp),
                        color = VitalTraceNavy.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Crossfade(
                    targetState = notification.isRead,
                    modifier = Modifier.padding(top = 8.dp),
                    animationSpec = tween(180),
                    label = "read-state"
                ) { isRead ->
                    if (isRead) {
                        Text(
                            text = stringResource(R.string.notifications_status_read),
                            color = VitalTraceNavy.copy(alpha = 0.66f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(7.dp),
                                shape = CircleShape,
                                color = accent
                            ) {}
                            Text(
                                text = stringResource(R.string.notifications_status_new),
                                modifier = Modifier.padding(start = 7.dp),
                                color = accent,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationIcon(category: NotificationCategory, color: Color) {
    Surface(
        modifier = Modifier.size(38.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.12f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = category.icon,
                contentDescription = stringResource(category.descriptionResource),
                modifier = Modifier.size(19.dp),
                tint = color
            )
        }
    }
}

private enum class NotificationCategory(
    val icon: ImageVector,
    val descriptionResource: Int
) {
    APPOINTMENT(Icons.Outlined.CalendarMonth, R.string.notifications_category_appointment),
    MEASUREMENT(Icons.Outlined.MonitorHeart, R.string.notifications_category_measurement),
    TREATMENT(Icons.Outlined.Medication, R.string.notifications_category_treatment),
    GENERAL(Icons.Outlined.Notifications, R.string.notifications_category_general),
    ALERT(Icons.Outlined.Warning, R.string.notifications_category_alert)
}
@Composable
private fun categoryColor(category: NotificationCategory): Color = when (category) {
    NotificationCategory.APPOINTMENT -> MaterialTheme.colorScheme.primary
    NotificationCategory.MEASUREMENT -> VitalTraceTeal
    NotificationCategory.TREATMENT -> MaterialTheme.colorScheme.tertiary
    NotificationCategory.GENERAL -> MaterialTheme.colorScheme.secondary
    NotificationCategory.ALERT -> MaterialTheme.colorScheme.error
}

private fun notificationCategory(notification: PatientNotification): NotificationCategory {
    val value = (notification.type.orEmpty() + " " + notification.relatedType.orEmpty()).uppercase()
    return when {
        "APPOINTMENT" in value -> NotificationCategory.APPOINTMENT
        "MEASUREMENT" in value -> NotificationCategory.MEASUREMENT
        "TREATMENT" in value -> NotificationCategory.TREATMENT
        "ALERT" in value -> NotificationCategory.ALERT
        else -> NotificationCategory.GENERAL
    }
}

@Composable
private fun NotificationsSkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(4) { index ->
            NotificationSkeletonCard(accentWidth = if (index % 2 == 0) 0.72f else 0.58f)
        }
    }
}

@Composable
private fun NotificationSkeletonCard(accentWidth: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(VitalTraceTeal.copy(alpha = 0.10f), CircleShape)
                )
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth(accentWidth)
                            .height(15.dp)
                            .background(VitalTraceNavy.copy(alpha = 0.09f), CircleShape)
                    )
                    Box(
                        Modifier
                            .padding(top = 9.dp)
                            .fillMaxWidth(0.32f)
                            .height(10.dp)
                            .background(VitalTraceNavy.copy(alpha = 0.06f), CircleShape)
                    )
                }
            }
            Box(
                Modifier
                    .padding(top = 18.dp)
                    .fillMaxWidth()
                    .height(11.dp)
                    .background(VitalTraceNavy.copy(alpha = 0.06f), CircleShape)
            )
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(0.68f)
                    .height(11.dp)
                    .background(VitalTraceNavy.copy(alpha = 0.06f), CircleShape)
            )
        }
    }
}

@Composable
private fun NotificationsEmptyState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StateIcon(icon = Icons.Outlined.Notifications, description = stringResource(R.string.notifications_empty_icon))
        Text(
            text = stringResource(R.string.notifications_empty_title),
            modifier = Modifier.padding(top = 20.dp),
            color = VitalTraceNavy,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.notifications_empty_description),
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRefresh,
            modifier = Modifier.padding(top = 22.dp).height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.notifications_refresh), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun NotificationsErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StateIcon(icon = Icons.Outlined.Warning, description = stringResource(R.string.notifications_error_icon))
        Text(
            text = stringResource(R.string.notifications_error_title),
            modifier = Modifier.padding(top = 20.dp),
            color = VitalTraceNavy,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 22.dp).height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(stringResource(R.string.notifications_retry), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StateIcon(icon: ImageVector, description: String) {
    Surface(
        modifier = Modifier.size(84.dp),
        shape = CircleShape,
        color = VitalTraceTeal.copy(alpha = 0.10f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                modifier = Modifier.size(40.dp),
                tint = VitalTraceTeal
            )
        }
    }
}



