package com.vitaltrace.app.feature.appointments.reminders

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentReminderStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Synchronized
    fun knownAppointmentIds(): Set<Long> = preferences
        .getStringSet(KEY_KNOWN_APPOINTMENTS, emptySet())
        .orEmpty()
        .mapNotNull(String::toLongOrNull)
        .toSet()

    @Synchronized
    fun updateKnownAppointmentIds(ids: Set<Long>, replace: Boolean) {
        val updated = if (replace) ids else knownAppointmentIds() + ids
        preferences.edit()
            .putStringSet(KEY_KNOWN_APPOINTMENTS, updated.map(Long::toString).toSet())
            .apply()
    }

    @Synchronized
    fun setActive(workName: String, scheduledAt: String?) {
        val activeWork = preferences.getStringSet(KEY_ACTIVE_WORK, emptySet()).orEmpty().toMutableSet()
        activeWork.removeAll { it.startsWith("$workName|") }
        if (scheduledAt != null) activeWork.add("$workName|$scheduledAt")
        preferences.edit().putStringSet(KEY_ACTIVE_WORK, activeWork).apply()
    }

    @Synchronized
    fun isActive(workName: String, scheduledAt: String): Boolean =
        preferences.getStringSet(KEY_ACTIVE_WORK, emptySet())
            .orEmpty()
            .contains("$workName|$scheduledAt")

    @Synchronized
    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "appointment_reminder_state"
        const val KEY_KNOWN_APPOINTMENTS = "known_appointment_ids"
        const val KEY_ACTIVE_WORK = "active_work_names"
    }
}