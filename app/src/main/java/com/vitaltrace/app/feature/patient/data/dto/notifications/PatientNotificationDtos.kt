package com.vitaltrace.app.feature.patient.data.dto.notifications

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientNotificationDto(
    val id: Long,
    val type: String? = null,
    val title: String? = null,
    val message: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("read_at") val readAt: String? = null,
    @SerialName("related_type") val relatedType: String? = null,
    @SerialName("related_id") val relatedId: Long? = null,
    @SerialName("action_route") val actionRoute: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class UnreadNotificationsCountDto(
    @SerialName("unread_count") val unreadCount: Int = 0
)

@Serializable
data class MarkAllNotificationsReadDto(
    @SerialName("updated_count") val updatedCount: Int = 0,
    @SerialName("unread_count") val unreadCount: Int = 0
)

