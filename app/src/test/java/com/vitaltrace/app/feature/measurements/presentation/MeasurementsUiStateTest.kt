package com.vitaltrace.app.feature.measurements.presentation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class MeasurementsUiStateTest {
    @Test
    fun `attention filter keeps only values outside reference range`() {
        val state = stateWith(
            measurement(1, "Glucosa en sangre", "110", MeasurementRisk.NORMAL),
            measurement(2, "Glucosa en sangre", "212", MeasurementRisk.HIGH)
        ).copy(attentionOnly = true, selectedPeriod = MeasurementPeriod.ALL)

        assertEquals(listOf(2L), state.visibleMeasurements.map(MeasurementUiModel::id))
    }

    @Test
    fun `query and type filters can be combined`() {
        val state = stateWith(
            measurement(1, "Glucosa en sangre", "110", MeasurementRisk.NORMAL, "Antes del desayuno"),
            measurement(2, "Saturación de oxígeno", "97", MeasurementRisk.NORMAL, "En reposo")
        ).copy(
            query = "reposo",
            selectedTypeName = "Saturación de oxígeno",
            selectedPeriod = MeasurementPeriod.ALL
        )

        assertEquals(listOf(2L), state.visibleMeasurements.map(MeasurementUiModel::id))
    }

    @Test
    fun `all period exposes complete chart without date overflow`() {
        val state = stateWith(
            measurement(1, "Glucosa en sangre", "110", MeasurementRisk.NORMAL)
        ).copy(selectedPeriod = MeasurementPeriod.ALL)

        assertEquals(1, state.chartMeasurements.size)
    }

    private fun stateWith(vararg values: MeasurementUiModel): MeasurementsUiState = MeasurementsUiState(
        contentState = MeasurementsContentState.Success(
            MeasurementsContentUiModel(
                latestMeasurement = null,
                measurements = values.toList(),
                currentPage = 1,
                lastPage = 1
            )
        )
    )

    private fun measurement(
        id: Long,
        type: String,
        value: String,
        risk: MeasurementRisk,
        observation: String = ""
    ) = MeasurementUiModel(
        id = id,
        typeName = type,
        value = value,
        unit = "u",
        date = "2026-09-24",
        time = "09:00:00",
        observation = observation,
        status = MeasurementStatus.PENDING,
        reviewerName = null,
        reviewedAt = null,
        reviewObservation = null,
        numericValue = value.toDouble(),
        measuredAtRaw = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        risk = risk
    )
}
