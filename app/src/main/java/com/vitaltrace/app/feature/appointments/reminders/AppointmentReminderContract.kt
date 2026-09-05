package com.vitaltrace.app.feature.appointments.reminders

internal object AppointmentReminderContract {
    const val CHANNEL_ID = "appointment_reminders"
    const val WORK_TAG = "appointment_reminders"
    const val INPUT_APPOINTMENT_ID = "appointment_id"
    const val INPUT_DAYS_BEFORE = "days_before"
    const val INPUT_SCHEDULED_AT = "scheduled_at"
    val DAYS_BEFORE = listOf(10, 7, 5, 3, 1)

    fun workName(appointmentId: Long, daysBefore: Int): String =
        "appointment_reminder_${appointmentId}_${daysBefore}d"
}