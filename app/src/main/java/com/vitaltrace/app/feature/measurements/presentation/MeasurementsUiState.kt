package com.vitaltrace.app.feature.measurements.presentation

data class MeasurementsUiState(
    val selectedFilter: MeasurementFilter = MeasurementFilter.ALL,
    val latestMeasurement: MeasurementUiModel? = null,
    val measurements: List<MeasurementUiModel> = emptyList(),
    val selectedMeasurementDetail: MeasurementDetailUiModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredMeasurements: List<MeasurementUiModel>
        get() = measurements.filter { measurement ->
            when (selectedFilter) {
                MeasurementFilter.ALL -> true
                MeasurementFilter.PENDING -> measurement.status != MeasurementStatus.REVIEWED
                MeasurementFilter.REVIEWED -> measurement.status == MeasurementStatus.REVIEWED
            }
        }
}

data class MeasurementDetailUiModel(
    val id: String,
    val value: String,
    val date: String,
    val time: String,
    val observation: String,
    val status: MeasurementStatus,
    val followUp: MeasurementFollowUpUiModel?
)

data class MeasurementFollowUpUiModel(
    val message: String,
    val date: String,
    val time: String
)

data class MeasurementUiModel(
    val id: String,
    val value: String,
    val date: String,
    val time: String,
    val status: MeasurementStatus
)

enum class MeasurementFilter {
    ALL,
    PENDING,
    REVIEWED
}

enum class MeasurementStatus {
    REGISTERED,
    PENDING,
    IN_REVIEW,
    REVIEWED
}
