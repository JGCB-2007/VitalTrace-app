package com.vitaltrace.app.feature.notifications.presentation

import com.vitaltrace.app.feature.patient.domain.model.PatientNotification

data class NotificationsUiState(
    val contentState: NotificationsContentState = NotificationsContentState.Loading,
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val unreadCount: Int = 0,
    val isLoadingMore: Boolean = false,
    val isMarkingAllAsRead: Boolean = false,
    val feedbackMessage: String? = null
)

sealed interface NotificationsContentState {
    data object Loading : NotificationsContentState
    data object Empty : NotificationsContentState
    data class Success(
        val notifications: List<PatientNotification>,
        val currentPage: Int,
        val lastPage: Int
    ) : NotificationsContentState
    data class Error(val message: String) : NotificationsContentState
}

enum class NotificationFilter(val apiValue: String) {
    ALL("all"),
    UNREAD("unread"),
    READ("read")
}

