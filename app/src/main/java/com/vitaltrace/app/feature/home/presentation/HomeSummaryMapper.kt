package com.vitaltrace.app.feature.home.presentation

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.core.presentation.formatClinicalDateTime
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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
                latest.toUiModel(trendPointsFor(sameTypeHistory, latest.measurementTypeId))
            },
            healthStatus = when {
                summary.alerts.critical > 0 -> HealthStatusUiModel(
                    HealthStatusLevel.CRITICAL,
                    "Requiere atención",
                    "Hay ${summary.alerts.critical} alerta(s) crítica(s) pendiente(s) de revisión.",
                    summary.alerts.open
                )
                summary.alerts.open > 0 -> HealthStatusUiModel(
                    HealthStatusLevel.ATTENTION,
                    "Seguimiento pendiente",
                    "Tienes ${summary.alerts.open} alerta(s) abierta(s) en seguimiento.",
                    summary.alerts.open
                )
                else -> HealthStatusUiModel()
            },
            activeTreatmentsCount = summary.activeTreatments.size
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
            status = appointmentStatusLabel(status)
        )
    }

    private fun Measurement.toUiModel(trendPoints: List<MeasurementTrendPoint>): RecentMeasurementUiModel {
        return RecentMeasurementUiModel(
            typeName = measurementType?.name?.localizedMeasurementTypeName().orEmpty()
                .ifBlank { "Medición" },
            value = formattedValue(),
            unit = unit,
            date = formatClinicalDateTime(measuredAt),
            trendPoints = trendPoints
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

    private fun trendPointsFor(measurements: List<Measurement>, typeId: Long): List<MeasurementTrendPoint> {
        val values = measurements
            .filter { it.measurementTypeId == typeId }
            .mapNotNull { measurement ->
                measurement.value.toFloatOrNull()?.let { value -> Triple(measurement, measurement.measuredAt, value) }
            }
            .sortedBy { it.second }
            .takeLast(7)
        if (values.isEmpty()) return emptyList()
        val minimum = values.minOf { it.third }
        val range = values.maxOf { it.third } - minimum
        return values.map { (measurement, measuredAt, value) ->
            MeasurementTrendPoint(
                normalizedValue = if (range == 0f) 0.65f else 0.25f + ((value - minimum) / range) * 0.75f,
                displayValue = measurement.formattedValue(),
                dateLabel = compactTrendDateLabel(measuredAt)
            )
        }
    }

    private fun compactTrendDateLabel(value: String): String {
        val rawDate = value.trim().substringBefore(" ")
        return runCatching {
            LocalDate.parse(rawDate, DateTimeFormatter.ISO_LOCAL_DATE)
                .format(DateTimeFormatter.ofPattern("d/M", Locale.getDefault()))
        }.getOrElse {
            rawDate.takeLast(5).replace('-', '/').trimStart('0')
        }
    }

    private fun String.localizedMeasurementTypeName(): String = when (trim().lowercase()) {
        "systolic blood pressure" -> "Presión arterial sistólica"
        "blood glucose" -> "Glucosa en sangre"
        "oxygen saturation" -> "Saturación de oxígeno"
        else -> this
    }

    private fun formatAppointmentDateTime(value: String): Pair<String, String> {
        val parsed = runCatching {
            LocalDateTime.parse(value.trim(), API_DATE_TIME_FORMAT)
        }.getOrNull() ?: return value.substringBefore(" ") to value.substringAfter(" ", "")
        val locale = Locale.getDefault()
        val date = parsed.format(DateTimeFormatter.ofPattern("d MMM uuuu", locale)).lowercase(locale)
        val time = parsed.format(DateTimeFormatter.ofPattern("h:mm a", locale)).lowercase(locale)
        return date to time
    }

    private fun appointmentStatusLabel(value: String): String = when (value.trim().uppercase()) {
        "SCHEDULED" -> "Programada"
        "CONFIRMED" -> "Confirmada"
        "ATTENDED" -> "Atendida"
        "NO_SHOW" -> "No asisti\u00f3"
        "CANCELLED" -> "Cancelada"
        else -> value
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
            in 5..11 -> "Buenos d\u00edas"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    private companion object {
        val API_DATE_TIME_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}
