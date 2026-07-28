package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,

    @SerialName("person_id")
    val personId: Int,

    val email: String,
    val status: String,

    @SerialName("email_verified_at")
    val emailVerifiedAt: String? = null,

    @SerialName("last_access_at")
    val lastAccessAt: String? = null,

    @SerialName("failed_attempts")
    val failedAttempts: Int = 0,

    @SerialName("blocked_until")
    val blockedUntil: String? = null,

    val person: PersonDto,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("created_by")
    val createdBy: Int? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("updated_by")
    val updatedBy: Int? = null,

    @SerialName("deleted_at")
    val deletedAt: String? = null,

    @SerialName("deleted_by")
    val deletedBy: Int? = null
)