package com.vitaltrace.app.feature.measurements.presentation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class MeasurementsUiState(
    val contentState: MeasurementsContentState = MeasurementsContentState.Loading,
    val selectedMeasurementDetail: MeasurementDetailUiModel? = null,
    val selectedFilter: MeasurementFilter = MeasurementFilter.ALL,
    val query: String = "",
    val selectedTypeName: String? = null,
    val selectedPeriod: MeasurementPeriod = MeasurementPeriod.DAYS_30,
    val attentionOnly: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false
) {
    val visibleMeasurements: List<MeasurementUiModel>
        get() {
            val content = (contentState as? MeasurementsContentState.Success)?.content
                ?: return emptyList()
            val all = listOfNotNull(content.latestMeasurement) + content.measurements
            val byReview = when (selectedFilter) {
                MeasurementFilter.ALL -> content.measurements
                MeasurementFilter.PENDING -> all.filter { it.status == MeasurementStatus.PENDING }
                MeasurementFilter.REVIEWED -> all.filter { it.status == MeasurementStatus.REVIEWED }
            }
            val threshold = selectedPeriod.takeUnless { it == MeasurementPeriod.ALL }
                ?.let { LocalDateTime.now().minusDays(it.days.toLong()) }
            return byReview.filter { measurement ->
                (query.isBlank() || listOf(measurement.typeName, measurement.observation, measurement.value, measurement.unit)
                    .any { it.contains(query.trim(), ignoreCase = true) }) &&
                    (selectedTypeName == null || measurement.typeName == selectedTypeName) &&
                    (!attentionOnly || measurement.risk in setOf(MeasurementRisk.LOW, MeasurementRisk.HIGH)) &&
                    (threshold == null || measurement.measuredDateTime()?.isAfter(threshold) != false)
            }
        }

    val allMeasurements: List<MeasurementUiModel>
        get() = (contentState as? MeasurementsContentState.Success)?.content?.let {
            (listOfNotNull(it.latestMeasurement) + it.measurements).distinctBy(MeasurementUiModel::id)
        }.orEmpty()

    val availableTypes: List<String>
        get() = allMeasurements.map(MeasurementUiModel::typeName).distinct().sorted()

    val chartMeasurements: List<MeasurementUiModel>
        get() {
            val chosenType = selectedTypeName ?: allMeasurements.firstOrNull()?.typeName
            val threshold = selectedPeriod.takeUnless { it == MeasurementPeriod.ALL }
                ?.let { LocalDateTime.now().minusDays(it.days.toLong()) }
            return allMeasurements.filter {
                it.typeName == chosenType &&
                    (threshold == null || it.measuredDateTime()?.isAfter(threshold) != false)
            }.sortedBy(MeasurementUiModel::measuredAtRaw)
        }
}

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
    val observation: String,
    val status: MeasurementStatus,
    val reviewerName: String?,
    val reviewedDate: String?,
    val reviewedTime: String?,
    val reviewObservation: String?
)

data class MeasurementUiModel(
    val id: Long,
    val typeName: String,
    val value: String,
    val unit: String,
    val date: String,
    val time: String,
    val observation: String,
    val status: MeasurementStatus,
    val reviewerName: String?,
    val reviewedAt: String?,
    val reviewObservation: String?,
    val numericValue: Double? = value.toDoubleOrNull(),
    val measuredAtRaw: String = "$date $time",
    val risk: MeasurementRisk = MeasurementRisk.UNKNOWN
) {
    fun toDetail() = MeasurementDetailUiModel(
        id = id,
        typeName = typeName,
        value = value,
        unit = unit,
        date = date,
        time = time,
        observation = observation,
        status = status,
        reviewerName = reviewerName,
        reviewedDate = reviewedAt?.substringBefore(" "),
        reviewedTime = reviewedAt?.substringAfter(" ", ""),
        reviewObservation = reviewObservation
    )
}

fun MeasurementUiModel.measuredDateTime(): LocalDateTime? = runCatching {
    LocalDateTime.parse(measuredAtRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}.getOrNull()

enum class MeasurementFilter {
    ALL,
    PENDING,
    REVIEWED
}

enum class MeasurementPeriod(val days: Int) {
    DAYS_7(7), DAYS_30(30), DAYS_90(90), ALL(Int.MAX_VALUE)
}

enum class MeasurementRisk { LOW, NORMAL, HIGH, UNKNOWN }

enum class MeasurementStatus {
    PENDING,
    REVIEWED,
    UNKNOWN;

    companion object {
        fun fromApiValue(value: String): MeasurementStatus {
            return entries.firstOrNull { it.name == value } ?: UNKNOWN
        }
    }
}
