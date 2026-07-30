package com.vitaltrace.app.feature.measurements.presentation

import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import javax.inject.Inject

class MeasurementsMapper @Inject constructor() {
    fun map(page: Page<Measurement>): MeasurementsContentUiModel {
        val measurements = page.items.map { it.toUiModel() }
        return MeasurementsContentUiModel(
            latestMeasurement = measurements.firstOrNull(),
            measurements = measurements.drop(1),
            currentPage = page.meta.currentPage,
            lastPage = page.meta.lastPage
        )
    }

    private fun Measurement.toUiModel(): MeasurementUiModel {
        val dateTimeParts = measuredAt.trim().split(" ", limit = 2)
        return MeasurementUiModel(
            id = id,
            typeName = measurementType?.name.orEmpty(),
            value = value,
            unit = unit,
            date = dateTimeParts.firstOrNull().orEmpty(),
            time = dateTimeParts.getOrNull(1).orEmpty(),
            observation = observation.orEmpty()
        )
    }
}
