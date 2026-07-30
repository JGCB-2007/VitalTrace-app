package com.vitaltrace.app.feature.profile.presentation

data class ProfileUiState(
    val user: ProfileUserUiModel? = null,
    val notificationSettings: NotificationSettingsUiModel = NotificationSettingsUiModel(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class ProfileUserUiModel(
    val fullName: String,
    val initials: String,
    val identifier: String,
    val email: String,
    val phone: String?,
    val dateOfBirth: String?,
    val age: Int?,
    val gender: String?,
    val address: String?,
    val identificationNumber: String?,
    val emergencyContactName: String?,
    val emergencyContactPhone: String?,
    val accountStatus: String,
    val administrativeStatus: String
)

data class NotificationSettingsUiModel(
    val measurementRemindersEnabled: Boolean = true,
    val appointmentNotificationsEnabled: Boolean = true,
    val emailUpdatesEnabled: Boolean = false
)
