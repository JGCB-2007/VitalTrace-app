package com.vitaltrace.app.feature.measurements.presentation

import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import javax.inject.Inject

class MeasurementsMapper @Inject constructor() {
    fun map(page: Page<Measurement>): MeasurementsContentUiModel {
        return map(page.items).copy(
            currentPage = page.meta.currentPage,
            lastPage = page.meta.lastPage
        )
    }

    fun map(items: List<Measurement>): MeasurementsContentUiModel {
        val measurements = items.map { it.toUiModel() }
        return mapUiModels(measurements)
    }

    fun mapUiModels(measurements: List<MeasurementUiModel>): MeasurementsContentUiModel {
        return MeasurementsContentUiModel(
            latestMeasurement = measurements.firstOrNull(),
            measurements = measurements.drop(1),
            currentPage = 1,
            lastPage = 1
        )
    }

    private fun Measurement.toUiModel(): MeasurementUiModel {
        val dateTimeParts = measuredAt.trim().split(" ", limit = 2)
        return MeasurementUiModel(
            id = id,
            typeName = measurementType?.name.orEmpty().localizedMeasurementTypeName(),
            value = value,
            unit = unit,
            date = dateTimeParts.firstOrNull().orEmpty(),
            time = dateTimeParts.getOrNull(1).orEmpty(),
            observation = observation.orEmpty(),
            status = MeasurementStatus.fromApiValue(reviewStatus),
            reviewerName = reviewer?.fullName,
            reviewedAt = reviewedAt,
            reviewObservation = reviewObservation,
            numericValue = value.toDoubleOrNull(),
            measuredAtRaw = measuredAt,
            risk = measurementRisk(measurementType?.name.orEmpty(), value.toDoubleOrNull())
        )
    }

    private fun String.localizedMeasurementTypeName(): String = when (trim().lowercase()) {
        "systolic blood pressure" -> "Presión arterial sistólica"
        "blood glucose" -> "Glucosa en sangre"
        "oxygen saturation" -> "Saturación de oxígeno"
        else -> this
    }

    private fun measurementRisk(typeName: String, value: Double?): MeasurementRisk {
        value ?: return MeasurementRisk.UNKNOWN
        return when (typeName.trim().lowercase()) {
            "systolic blood pressure" -> when { value < 90 -> MeasurementRisk.LOW; value > 140 -> MeasurementRisk.HIGH; else -> MeasurementRisk.NORMAL }
            "blood glucose" -> when { value < 70 -> MeasurementRisk.LOW; value > 180 -> MeasurementRisk.HIGH; else -> MeasurementRisk.NORMAL }
            "oxygen saturation" -> when { value < 92 -> MeasurementRisk.LOW; value <= 100 -> MeasurementRisk.NORMAL; else -> MeasurementRisk.HIGH }
            else -> MeasurementRisk.UNKNOWN
        }
    }
}
