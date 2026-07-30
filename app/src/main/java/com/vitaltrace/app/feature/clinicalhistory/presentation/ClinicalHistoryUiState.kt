package com.vitaltrace.app.feature.clinicalhistory.presentation

import com.vitaltrace.app.feature.patient.domain.model.ClinicalHistory

sealed interface ClinicalHistoryUiState {
    data object Loading : ClinicalHistoryUiState
    data class Success(val history: ClinicalHistory) : ClinicalHistoryUiState
    data class Empty(val recordNumber: String) : ClinicalHistoryUiState
    data class Error(val message: String) : ClinicalHistoryUiState
}
