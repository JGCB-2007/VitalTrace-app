package com.vitaltrace.app.feature.measurements.presentation.form

/**
 * Temporary Android-side mirror of the active measurement type catalog deployed in production.
 *
 * The patient API does not currently expose a readable catalog endpoint. Keep these IDs, names,
 * units, and precision aligned with production until Android can consume an API-owned catalog.
 */
internal object LocalMeasurementTypeCatalog {
    val types = listOf(
        MeasurementTypeOption(id = 1L, name = "Systolic blood pressure", unit = "mmHg", decimals = 0),
        MeasurementTypeOption(id = 2L, name = "Blood glucose", unit = "mg/dL", decimals = 0),
        MeasurementTypeOption(id = 3L, name = "Oxygen saturation", unit = "%", decimals = 0)
    )
}