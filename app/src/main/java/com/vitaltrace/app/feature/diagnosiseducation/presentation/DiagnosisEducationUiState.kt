package com.vitaltrace.app.feature.diagnosiseducation.presentation

import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducation

sealed interface DiagnosisEducationUiState {
    data object Loading : DiagnosisEducationUiState
    data class Success(val education: DiagnosisEducation) : DiagnosisEducationUiState
    data class Empty(val education: DiagnosisEducation) : DiagnosisEducationUiState
    data object Error : DiagnosisEducationUiState
}
