package com.vitaltrace.app.feature.appointments.reminders

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AppointmentReminderPlannerTest {
    private val zone = ZoneId.of("America/Managua")
    private val now = ZonedDateTime.of(2026, 9, 1, 10, 0, 0, 0, zone)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    @Test fun `appointment in 20 days schedules all reminders`() =
        assertOffsets(20, expected = listOf(10, 7, 5, 3, 1))

    @Test fun `appointment in 8 days skips ten day reminder`() =
        assertOffsets(8, expected = listOf(7, 5, 3, 1))

    @Test fun `appointment in 4 days schedules three and one day reminders`() =
        assertOffsets(4, expected = listOf(3, 1))

    @Test fun `appointment in 2 days schedules only one day reminder`() =
        assertOffsets(2, expected = listOf(1))

    @Test fun `appointment in 12 hours schedules no reminder`() =
        assertOffsets(hours = 12, expected = emptyList())

    @Test
    fun `cancelled appointment schedules no reminder`() {
        assertEquals(emptyList<AppointmentReminderPlan>(), plans(days = 20, status = " CANCELLED "))
    }

    @Test
    fun `status comparison is normalized`() {
        assertEquals(
            listOf(10, 7, 5, 3, 1),
            plans(days = 20, status = " confirmed ").map { it.daysBefore }
        )
    }

    private fun assertOffsets(days: Long = 0, hours: Long = 0, expected: List<Int>) {
        assertEquals(expected, plans(days = days, hours = hours).map { it.daysBefore })
    }

    private fun plans(
        days: Long = 0,
        hours: Long = 0,
        status: String = "SCHEDULED"
    ): List<AppointmentReminderPlan> {
        val appointmentAt = now.plusDays(days).plusHours(hours)
        return AppointmentReminderPlanner.createPlans(
            appointment = Appointment(
                id = 25,
                scheduledAt = appointmentAt.toLocalDateTime().format(formatter),
                durationMinutes = 30,
                reason = "",
                status = status,
                professional = null
            ),
            now = now,
            zoneId = zone
        )
    }
}