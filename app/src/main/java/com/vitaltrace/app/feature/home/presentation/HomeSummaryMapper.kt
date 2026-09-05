package com.vitaltrace.app.feature.home.presentation

import com.vitaltrace.app.core.presentation.localization.EnumDisplayEs
import com.vitaltrace.app.core.presentation.localization.SpanishDateTime
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import java.math.RoundingMode
import java.time.LocalTime
import javax.inject.Inject

class HomeSummaryMapper @Inject constructor() {
    fun map(
        summary: PatientSummary,
        chartMeasurements: List<Measurement> = summary.latestMeasurements
    ): HomeContentUiModel {
        val latestMeasurement = summary.latestMeasurements.firstOrNull()
        return HomeContentUiModel(
            greeting = greetingFor(LocalTime.now()),
            patientName = summary.patient.fullName.orEmpty(),
            patientInitials = initialsFor(summary.patient.fullName),
            followUpStatus = latestMeasurement?.toFollowUpStatus(),
            nextAppointment = summary.nextAppointment?.toUiModel(),
            recentMeasurement = latestMeasurement?.let { latest ->
                val sameTypeHistory = chartMeasurements
                    .filter { it.measurementTypeId == latest.measurementTypeId }
                    .ifEmpty {
                        summary.latestMeasurements.filter {
                            it.measurementTypeId == latest.measurementTypeId
                        }
                    }
                latest.toUiModel(chartValuesFor(sameTypeHistory, latest.measurementTypeId))
            }
        )
    }

    private fun Appointment.toUiModel(): NextAppointmentUiModel {
        val formatted = formatAppointmentDateTime(scheduledAt)
        return NextAppointmentUiModel(
            id = id,
            professionalName = professional?.fullName.orEmpty(),
            reason = reason,
            date = formatted.first,
            time = formatted.second,
            status = EnumDisplayEs.appointmentStatus(status)
        )
    }

    private fun Measurement.toUiModel(chartValues: List<Float>): RecentMeasurementUiModel {
        return RecentMeasurementUiModel(
            value = formattedValue(),
            unit = unit,
            date = measuredAt.substringBefore(" "),
            chartValues = chartValues
        )
    }

    private fun Measurement.formattedValue(): String {
        val decimals = measurementType?.decimals ?: return value
        return value.toBigDecimalOrNull()
            ?.setScale(decimals.coerceAtLeast(0), RoundingMode.HALF_UP)
            ?.toPlainString()
            ?: value
    }

    private fun Measurement.toFollowUpStatus(): FollowUpStatusUiModel? {
        if (reviewStatus.trim().uppercase() != "PENDING") return null
        return FollowUpStatusUiModel(
            status = "En revisi\u00f3n",
            title = "Tu \u00faltima medici\u00f3n fue enviada para revisi\u00f3n.",
            description = "Un profesional podr\u00e1 revisarla pronto."
        )
    }

    private fun chartValuesFor(measurements: List<Measurement>, typeId: Long): List<Float> {
        val values = measurements
            .filter { it.measurementTypeId == typeId }
            .mapNotNull { measurement ->
                measurement.value.toFloatOrNull()?.let { measurement.measuredAt to it }
            }
            .sortedBy { it.first }
            .takeLast(7)
            .map { it.second }
        if (values.isEmpty()) return emptyList()
        val minimum = values.min()
        val range = values.max() - minimum
        return if (range == 0f) {
            List(values.size) { 0.65f }
        } else {
            values.map { value -> 0.25f + ((value - minimum) / range) * 0.75f }
        }
    }

    private fun formatAppointmentDateTime(value: String): Pair<String, String> =
        SpanishDateTime.formatApiDateTime(value)

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
            in 5..11 -> "Buenos d\u00edas"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }
}
