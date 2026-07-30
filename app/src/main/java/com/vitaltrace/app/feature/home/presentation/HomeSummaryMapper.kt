package com.vitaltrace.app.feature.home.presentation

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import java.time.LocalTime
import javax.inject.Inject

class HomeSummaryMapper @Inject constructor() {
    fun map(summary: PatientSummary): HomeContentUiModel {
        return HomeContentUiModel(
            greeting = greetingFor(LocalTime.now()),
            patientName = summary.patient.fullName.orEmpty(),
            patientInitials = initialsFor(summary.patient.fullName),
            followUpStatus = null,
            nextAppointment = summary.nextAppointment?.toUiModel(),
            recentMeasurement = summary.latestMeasurements.firstOrNull()?.toUiModel()
        )
    }

    private fun Appointment.toUiModel(): NextAppointmentUiModel {
        val dateTimeParts = scheduledAt.trim().split(" ", limit = 2)
        return NextAppointmentUiModel(
            professionalName = professional?.fullName.orEmpty(),
            reason = reason,
            date = dateTimeParts.firstOrNull().orEmpty(),
            time = dateTimeParts.getOrNull(1).orEmpty(),
            status = status
        )
    }

    private fun Measurement.toUiModel(): RecentMeasurementUiModel {
        return RecentMeasurementUiModel(
            value = value,
            unit = unit,
            date = measuredAt.substringBefore(" "),
            chartValues = emptyList()
        )
    }

    private fun initialsFor(fullName: String?): String {
        return fullName
            ?.trim()
            ?.split(Regex("\\s+"))
            ?.filter(String::isNotEmpty)
            ?.take(2)
            ?.mapNotNull { it.firstOrNull()?.uppercase() }
            ?.joinToString(separator = "")
            .orEmpty()
    }

    private fun greetingFor(time: LocalTime): String {
        return when (time.hour) {
            in 5..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }
}
