package com.vitaltrace.app.feature.patient.domain.model

data class PatientNotification(
    val id: Long,
    val type: String?,
    val title: String?,
    val message: String?,
    val isRead: Boolean,
    val readAt: String?,
    val relatedType: String?,
    val relatedId: Long?,
    val actionRoute: String?,
    val createdAt: String?
)

data class MarkAllNotificationsReadResult(
    val updatedCount: Int,
    val unreadCount: Int
)
