package com.vitaltrace.app.feature.appointments.presentation

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Page
import javax.inject.Inject

class AppointmentsMapper @Inject constructor() {
    fun map(page: Page<Appointment>): AppointmentsContentUiModel {
        val appointments = page.items.map { it.toUiModel() }
        val upcoming = appointments.filter { it.status.isUpcoming }
        val previous = appointments.filterNot { it.status.isUpcoming }
        val nextAppointment = upcoming.minByOrNull(AppointmentUiModel::scheduledAt)

        return AppointmentsContentUiModel(
            nextAppointment = nextAppointment,
            upcomingAppointments = upcoming
                .filterNot { it.id == nextAppointment?.id }
                .sortedBy(AppointmentUiModel::scheduledAt),
            previousAppointments = previous,
            currentPage = page.meta.currentPage,
            lastPage = page.meta.lastPage
        )
    }

    private fun Appointment.toUiModel(): AppointmentUiModel {
        val dateTimeParts = scheduledAt.trim().split(" ", limit = 2)
        val professionalName = professional?.fullName.orEmpty()
        return AppointmentUiModel(
            id = id,
            professionalName = professionalName,
            professionalInitials = initialsFor(professionalName),
            specialty = professional?.specialty?.name.orEmpty(),
            reason = reason,
            date = dateTimeParts.firstOrNull().orEmpty(),
            time = dateTimeParts.getOrNull(1).orEmpty(),
            scheduledAt = scheduledAt,
            status = AppointmentStatus.fromApiValue(status)
        )
    }

    private fun initialsFor(fullName: String): String {
        return fullName
            .trim()
            .split(Regex("\\s+"))
            .filter(String::isNotEmpty)
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString(separator = "")
    }
}
