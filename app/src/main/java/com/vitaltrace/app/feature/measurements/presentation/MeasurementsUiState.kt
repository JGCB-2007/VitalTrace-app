package com.vitaltrace.app.feature.measurements.presentation

data class MeasurementsUiState(
    val contentState: MeasurementsContentState = MeasurementsContentState.Loading,
    val selectedMeasurementDetail: MeasurementDetailUiModel? = null
)

sealed interface MeasurementsContentState {
    data object Loading : MeasurementsContentState
    data class Success(val content: MeasurementsContentUiModel) : MeasurementsContentState
    data class Error(val message: String) : MeasurementsContentState
}

data class MeasurementsContentUiModel(
    val latestMeasurement: MeasurementUiModel?,
    val measurements: List<MeasurementUiModel>,
    val currentPage: Int,
    val lastPage: Int
)

data class MeasurementDetailUiModel(
    val id: Long,
    val typeName: String,
    val value: String,
    val unit: String,
    val date: String,
    val time: String,
    val observation: String
)

data class MeasurementUiModel(
    val id: Long,
    val typeName: String,
    val value: String,
    val unit: String,
    val date: String,
    val time: String,
    val observation: String
) {
    fun toDetail() = MeasurementDetailUiModel(
        id = id,
        typeName = typeName,
        value = value,
        unit = unit,
        date = date,
        time = time,
        observation = observation
    )
}

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
