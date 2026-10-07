package com.vitaltrace.app.feature.profile.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vitaltrace.app.feature.profile.presentation.NotificationSettingsUiModel
import com.vitaltrace.app.feature.profile.presentation.ReminderHistoryEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.notificationPreferencesDataStore by preferencesDataStore(
    name = "notification_preferences"
)

@Singleton
class NotificationPreferencesStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private companion object {
        val MEASUREMENT_REMINDERS = booleanPreferencesKey("measurement_reminders")
        val APPOINTMENT_NOTIFICATIONS = booleanPreferencesKey("appointment_notifications")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val REMINDER_DAYS = stringSetPreferencesKey("reminder_days")
        val SNOOZE_MINUTES = intPreferencesKey("snooze_minutes")
        val LAST_REMINDER_EVENT = stringPreferencesKey("last_reminder_event")
        val LAST_REMINDER_AT = longPreferencesKey("last_reminder_at")
        val REMINDER_HISTORY = stringPreferencesKey("reminder_history")
    }

    val settings: Flow<NotificationSettingsUiModel> =
        context.notificationPreferencesDataStore.data.map { preferences ->
            NotificationSettingsUiModel(
                measurementRemindersEnabled = preferences[MEASUREMENT_REMINDERS] ?: true,
                appointmentNotificationsEnabled = preferences[APPOINTMENT_NOTIFICATIONS] ?: true,
                reminderHour = (preferences[REMINDER_HOUR] ?: 9).coerceIn(0, 23),
                reminderMinute = (preferences[REMINDER_MINUTE] ?: 0).coerceIn(0, 59),
                reminderDays = preferences[REMINDER_DAYS]
                    ?.mapNotNull(String::toIntOrNull)
                    ?.filter { it in 1..7 }
                    ?.toSet()
                    ?.takeIf(Set<Int>::isNotEmpty)
                    ?: (1..7).toSet(),
                snoozeMinutes = (preferences[SNOOZE_MINUTES] ?: 30)
                    .takeIf { it in setOf(15, 30, 60) } ?: 30,
                lastReminderEvent = preferences[LAST_REMINDER_EVENT],
                lastReminderAt = preferences[LAST_REMINDER_AT],
                reminderHistory = preferences[REMINDER_HISTORY].orEmpty()
                    .split(';')
                    .mapNotNull { item ->
                        val parts = item.split('|', limit = 2)
                        parts.getOrNull(1)?.toLongOrNull()?.let { timestamp ->
                            ReminderHistoryEntry(parts.first(), timestamp)
                        }
                    }
            )
        }

    suspend fun setMeasurementReminders(enabled: Boolean) {
        context.notificationPreferencesDataStore.edit { it[MEASUREMENT_REMINDERS] = enabled }
    }

    suspend fun setAppointmentNotifications(enabled: Boolean) {
        context.notificationPreferencesDataStore.edit { it[APPOINTMENT_NOTIFICATIONS] = enabled }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.notificationPreferencesDataStore.edit {
            it[REMINDER_HOUR] = hour.coerceIn(0, 23)
            it[REMINDER_MINUTE] = minute.coerceIn(0, 59)
        }
    }

    suspend fun setReminderDays(days: Set<Int>) {
        val safeDays = days.filter { it in 1..7 }.toSet().ifEmpty { (1..7).toSet() }
        context.notificationPreferencesDataStore.edit {
            it[REMINDER_DAYS] = safeDays.map(Int::toString).toSet()
        }
    }

    suspend fun setSnoozeMinutes(minutes: Int) {
        context.notificationPreferencesDataStore.edit {
            it[SNOOZE_MINUTES] = minutes.takeIf { value -> value in setOf(15, 30, 60) } ?: 30
        }
    }

    suspend fun recordReminderEvent(event: String) {
        context.notificationPreferencesDataStore.edit {
            val timestamp = System.currentTimeMillis()
            it[LAST_REMINDER_EVENT] = event
            it[LAST_REMINDER_AT] = timestamp
            val history = it[REMINDER_HISTORY].orEmpty().split(';')
                .filter(String::isNotBlank)
                .plus("$event|$timestamp")
                .takeLast(10)
            it[REMINDER_HISTORY] = history.joinToString(";")
        }
    }
}
