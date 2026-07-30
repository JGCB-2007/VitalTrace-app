package com.vitaltrace.app.feature.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientNotificationsUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetUnreadNotificationsCountUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.MarkNotificationAsReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotifications: GetPatientNotificationsUseCase,
    private val getUnreadCount: GetUnreadNotificationsCountUseCase,
    private val markNotificationAsRead: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsRead: MarkAllNotificationsAsReadUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()
    private var listRequest: Job? = null
    private var countRequest: Job? = null

    init {
        loadNotifications()
        refreshUnreadCount()
    }

    fun retry() {
        loadNotifications()
        refreshUnreadCount()
    }

    fun selectFilter(filter: NotificationFilter) {
        if (filter == _uiState.value.selectedFilter) return
        listRequest?.cancel()
        _uiState.update { it.copy(selectedFilter = filter) }
        loadNotifications()
    }

    fun loadMore() {
        val content = _uiState.value.contentState as? NotificationsContentState.Success ?: return
        if (_uiState.value.isLoadingMore || content.currentPage >= content.lastPage) return
        loadNotifications(content.currentPage + 1)
    }

    fun markAsRead(notification: PatientNotification) {
        if (notification.isRead) return
        updateVisibleNotification(notification.id) { it.copy(isRead = true) }
        _uiState.update { it.copy(unreadCount = (it.unreadCount - 1).coerceAtLeast(0)) }
        viewModelScope.launch {
            markNotificationAsRead(notification.id).onFailure {
                updateVisibleNotification(notification.id) { notification }
                _uiState.update {
                    it.copy(
                        unreadCount = it.unreadCount + 1,
                        feedbackMessage = "No pudimos marcar la notificaciÃ³n como leÃ­da."
                    )
                }
            }
        }
    }

    fun markAllAsRead() {
        if (_uiState.value.unreadCount == 0 || _uiState.value.isMarkingAllAsRead) return
        _uiState.update { it.copy(isMarkingAllAsRead = true) }
        viewModelScope.launch {
            markAllNotificationsAsRead()
                .onSuccess { result ->
                    val content = _uiState.value.contentState
                    val updatedContent = if (content is NotificationsContentState.Success) {
                        content.copy(notifications = content.notifications.map { it.copy(isRead = true) })
                    } else content
                    _uiState.update {
                        it.copy(
                            contentState = updatedContent,
                            unreadCount = result.unreadCount,
                            isMarkingAllAsRead = false,
                            feedbackMessage = "Notificaciones marcadas como leÃ­das."
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isMarkingAllAsRead = false,
                            feedbackMessage = "No pudimos actualizar las notificaciones."
                        )
                    }
                }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun loadNotifications(page: Int = 1) {
        if (listRequest?.isActive == true) return
        if (page == 1) {
            _uiState.update { it.copy(contentState = NotificationsContentState.Loading) }
        } else {
            _uiState.update { it.copy(isLoadingMore = true) }
        }
        val filter = _uiState.value.selectedFilter
        listRequest = viewModelScope.launch {
            getNotifications(filter.apiValue, page = page)
                .onSuccess { result ->
                    val previous = (_uiState.value.contentState as? NotificationsContentState.Success)
                        ?.notifications.orEmpty()
                    val combined = if (page == 1) result.items else previous + result.items
                    _uiState.update {
                        it.copy(
                            contentState = if (combined.isEmpty()) {
                                NotificationsContentState.Empty
                            } else {
                                NotificationsContentState.Success(
                                    combined.distinctBy(PatientNotification::id),
                                    result.meta.currentPage,
                                    result.meta.lastPage
                                )
                            },
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            contentState = if (page == 1) {
                                NotificationsContentState.Error(
                                    "No pudimos cargar tus notificaciones."
                                )
                            } else it.contentState,
                            isLoadingMore = false,
                            feedbackMessage = if (page > 1) {
                                "No pudimos cargar mÃ¡s notificaciones."
                            } else null
                        )
                    }
                }
        }
    }

    private fun refreshUnreadCount() {
        if (countRequest?.isActive == true) return
        countRequest = viewModelScope.launch {
            getUnreadCount().onSuccess { count ->
                _uiState.update { it.copy(unreadCount = count.coerceAtLeast(0)) }
            }
        }
    }

    private fun updateVisibleNotification(
        id: Long,
        transform: (PatientNotification) -> PatientNotification
    ) {
        _uiState.update { state ->
            val content = state.contentState
            if (content !is NotificationsContentState.Success) return@update state
            state.copy(
                contentState = content.copy(
                    notifications = content.notifications.map {
                        if (it.id == id) transform(it) else it
                    }
                )
            )
        }
    }
}

