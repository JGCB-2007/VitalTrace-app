package com.vitaltrace.app.feature.treatments.presentation

data class TreatmentsUiState(
    val contentState: TreatmentsContentState = TreatmentsContentState.Loading,
    val selectedTreatment: TreatmentUiModel? = null
)

sealed interface TreatmentsContentState {
    data object Loading : TreatmentsContentState
    data class Success(val content: TreatmentsContentUiModel) : TreatmentsContentState
    data class Error(val message: String) : TreatmentsContentState
}

data class TreatmentsContentUiModel(
    val treatments: List<TreatmentUiModel>,
    val currentPage: Int,
    val lastPage: Int
)

data class TreatmentUiModel(
    val id: Long,
    val diagnosisDescription: String?,
    val diagnosisCode: String?,
    val indications: String,
    val startDate: String,
    val endDate: String?,
    val status: TreatmentStatus,
    val prescriberName: String?,
    val professionalType: String?,
    val specialtyName: String?
)

enum class TreatmentStatus {
    ACTIVE,
    FINISHED,
    SUSPENDED,
    UNKNOWN;

    companion object {
        fun fromApiValue(value: String): TreatmentStatus {
            return entries.firstOrNull { it.name == value } ?: UNKNOWN
        }
    }
}
