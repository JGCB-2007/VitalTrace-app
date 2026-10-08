package com.vitaltrace.app.feature.profile.presentation

import com.vitaltrace.app.core.settings.ThemePreference

data class ProfileUiState(
    val user: ProfileUserUiModel? = null,
    val notificationSettings: NotificationSettingsUiModel = NotificationSettingsUiModel(),
    val appSettings: AppSettingsUiModel = AppSettingsUiModel(),
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
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val reminderDays: Set<Int> = (1..7).toSet(),
    val snoozeMinutes: Int = 30,
    val lastReminderEvent: String? = null,
    val lastReminderAt: Long? = null,
    val reminderHistory: List<ReminderHistoryEntry> = emptyList()
)

data class ReminderHistoryEntry(val event: String, val timestamp: Long)

data class AppSettingsUiModel(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val secureScreenEnabled: Boolean = true,
    val avatarUri: String? = null,
    val largeTextEnabled: Boolean = false
)
