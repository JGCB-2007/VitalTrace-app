package com.vitaltrace.app.feature.appointments.reminders

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal data class AppointmentReminderPlan(
    val appointmentId: Long,
    val daysBefore: Int,
    val scheduledAt: LocalDateTime,
    val delay: Duration
)

internal object AppointmentReminderPlanner {
    private val appointmentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val reminderStatuses = setOf("SCHEDULED", "CONFIRMED")

    fun createPlans(
        appointment: Appointment,
        now: ZonedDateTime,
        zoneId: ZoneId = now.zone
    ): List<AppointmentReminderPlan> {
        if (appointment.status.trim().uppercase() !in reminderStatuses) return emptyList()

        val scheduledLocal = runCatching {
            LocalDateTime.parse(appointment.scheduledAt.trim(), appointmentFormatter)
        }.getOrNull() ?: return emptyList()
        val scheduled = scheduledLocal.atZone(zoneId)
        if (!scheduled.isAfter(now)) return emptyList()

        return AppointmentReminderContract.DAYS_BEFORE.mapNotNull { daysBefore ->
            val reminderAt = scheduled.minusDays(daysBefore.toLong())
            if (!reminderAt.isAfter(now)) return@mapNotNull null
            AppointmentReminderPlan(
                appointmentId = appointment.id,
                daysBefore = daysBefore,
                scheduledAt = scheduledLocal,
                delay = Duration.between(now, reminderAt)
            )
        }
    }
}