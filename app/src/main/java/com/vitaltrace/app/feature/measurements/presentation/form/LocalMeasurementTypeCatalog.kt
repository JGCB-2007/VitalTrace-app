package com.vitaltrace.app.feature.measurements.presentation.form

/**
 * Temporary Android-side mirror of the active measurement type catalog deployed in production.
 *
 * The patient API does not currently expose a readable catalog endpoint. Keep these IDs, names,
 * units, and precision aligned with production until Android can consume an API-owned catalog.
 */
internal object LocalMeasurementTypeCatalog {
    val types = listOf(
        MeasurementTypeOption(1L, "Presión arterial sistólica", "mmHg", 0, 30.0, 300.0),
        MeasurementTypeOption(2L, "Glucosa en sangre", "mg/dL", 0, 10.0, 1000.0),
        MeasurementTypeOption(3L, "Saturación de oxígeno", "%", 0, 1.0, 100.0)
    )
}
