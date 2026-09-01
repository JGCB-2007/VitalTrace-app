package com.vitaltrace.app.feature.relativeportal.presentation

import com.vitaltrace.app.feature.patient.domain.model.*
import com.vitaltrace.app.feature.relativeportal.domain.model.LinkedPatient

enum class RelativeSection(val label: String) {
    HOME("Inicio"), APPOINTMENTS("Citas"), MEASUREMENTS("Mediciones"), TREATMENTS("Tratamientos"), HISTORY("Historial")
}

data class RelativePortalContent(
    val summary: PatientSummary,
    val appointments: List<Appointment>,
    val measurements: List<Measurement>,
    val treatments: List<Treatment>,
    val clinicalHistory: ClinicalHistory
)

sealed interface RelativePortalUiState {
    data object Loading : RelativePortalUiState
    data object NoAuthorizedPatients : RelativePortalUiState
    data class Selecting(
        val patients: List<LinkedPatient>,
        val selectedPatientId: Long? = null
    ) : RelativePortalUiState
    data class Content(
        val patients: List<LinkedPatient>,
        val selected: LinkedPatient,
        val portal: RelativePortalContent,
        val section: RelativeSection = RelativeSection.HOME
    ) : RelativePortalUiState
    data class Error(val message: String) : RelativePortalUiState
}

sealed interface RelativePortalEffect { data object NavigateToLogin : RelativePortalEffect }
