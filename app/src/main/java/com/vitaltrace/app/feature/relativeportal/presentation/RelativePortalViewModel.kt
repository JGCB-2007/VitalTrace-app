package com.vitaltrace.app.feature.relativeportal.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.BuildConfig
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.SessionState
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import com.vitaltrace.app.feature.relativeportal.data.repository.RelativePortalException
import com.vitaltrace.app.feature.relativeportal.domain.model.LinkedPatient
import com.vitaltrace.app.feature.relativeportal.domain.repository.RelativeRepository
import com.vitaltrace.app.feature.relativeportal.domain.selection.RelativePatientSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelativePortalViewModel @Inject constructor(
    private val repository: RelativeRepository,
    private val selection: RelativePatientSelection,
    private val logoutUseCase: LogoutUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val mutableState = MutableStateFlow<RelativePortalUiState>(RelativePortalUiState.Loading)
    val state = mutableState.asStateFlow()
    private val effectChannel = Channel<RelativePortalEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init { loadPatients() }

    fun retry() = loadPatients()

    fun selectPatient(patient: LinkedPatient) {
        selection.select(patient.id)
        loadPortal(patient, patientsFromState())
    }

    fun changePatient() {
        val patients = patientsFromState()
        val selectedPatientId = (mutableState.value as? RelativePortalUiState.Content)?.selected?.id
        selection.clear()
        mutableState.value = if (patients.isEmpty()) {
            RelativePortalUiState.NoAuthorizedPatients
        } else {
            RelativePortalUiState.Selecting(patients, selectedPatientId)
        }
    }

    fun selectSection(section: RelativeSection) {
        val current = mutableState.value as? RelativePortalUiState.Content ?: return
        mutableState.value = current.copy(section = section)
    }

    fun logout() = viewModelScope.launch {
        logoutUseCase().onSuccess {
            selection.clear()
            effectChannel.send(RelativePortalEffect.NavigateToLogin)
        }
    }

    private fun loadPatients() {
        mutableState.value = RelativePortalUiState.Loading
        viewModelScope.launch {
            localLog("linkedPatients START")
            logResult("linkedPatients", repository.getLinkedPatients()).onSuccess { patients ->
                localLog("linkedPatients -> count=${patients.size}")
                when {
                    patients.isEmpty() -> {
                        selection.clear()
                        mutableState.value = RelativePortalUiState.NoAuthorizedPatients
                    }
                    selection.selectedId.value != null -> {
                        val selected = patients.firstOrNull { it.id == selection.selectedId.value }
                        if (selected == null) {
                            selection.clear()
                            mutableState.value = RelativePortalUiState.Selecting(patients)
                        } else loadPortal(selected, patients)
                    }
                    patients.size == 1 -> {
                        val selected = patients.single()
                        selection.select(selected.id)
                        localLog("selectedPatientId -> ${selected.id} (automatic)")
                        loadPortal(selected, patients)
                    }
                    else -> mutableState.value = RelativePortalUiState.Selecting(patients)
                }
            }.onFailure { mutableState.value = RelativePortalUiState.Error(it.message ?: "No pudimos cargar tus pacientes autorizados.") }
        }
    }

    private fun loadPortal(patient: LinkedPatient, patients: List<LinkedPatient>) {
        localLog("loading portal for patientId=${patient.id}")
        mutableState.value = RelativePortalUiState.Loading
        viewModelScope.launch {
            localLog("summary START")
            val summary = logResult("summary", repository.getSummary(patient.id))
                .getOrElse { return@launch portalFailure(it) }
            localLog("appointments START")
            val appointments = logResult("appointments", repository.getAppointments(patient.id))
                .getOrElse { return@launch portalFailure(it) }
            localLog("measurements START")
            val measurements = logResult("measurements", repository.getMeasurements(patient.id))
                .getOrElse { return@launch portalFailure(it) }
            localLog("treatments START")
            val treatments = logResult("treatments", repository.getTreatments(patient.id))
                .getOrElse { return@launch portalFailure(it) }
            localLog("clinicalHistory START")
            val history = logResult("clinicalHistory", repository.getClinicalHistory(patient.id))
                .getOrElse { return@launch portalFailure(it) }
            mutableState.value = RelativePortalUiState.Content(
                patients, patient,
                RelativePortalContent(summary, appointments.items, measurements.items, treatments.items, history),
                relativeName = (sessionManager.state.value as? SessionState.Authenticated)
                    ?.user
                    ?.fullName
                    .orEmpty()
            )
        }
    }

    private fun portalFailure(error: Throwable) {
        localLog("portal load failed -> ${error::class.simpleName}: ${error.message}")
        when ((error as? RelativePortalException)?.httpCode) {
            401 -> viewModelScope.launch {
                logoutUseCase()
                selection.clear()
                effectChannel.send(RelativePortalEffect.NavigateToLogin)
            }
            403 -> loadPatients()
            else -> mutableState.value = RelativePortalUiState.Error(
                error.message ?: "No pudimos cargar el portal familiar."
            )
        }
    }

    private fun patientsFromState(): List<LinkedPatient> = when (val current = mutableState.value) {
        is RelativePortalUiState.Content -> current.patients
        is RelativePortalUiState.Selecting -> current.patients
        else -> emptyList()
    }

    private fun localLog(message: String) {
        if (BuildConfig.BUILD_TYPE == "local") Log.d("VitalTraceRelative", message)
    }

    private fun <T> logResult(operation: String, result: Result<T>): Result<T> {
        result.fold(
            onSuccess = { localLog("$operation SUCCESS") },
            onFailure = { error ->
                val rootCause = generateSequence(error) { it.cause }.last()
                val httpCode = (error as? RelativePortalException)?.httpCode
                localLog(
                    "$operation FAILURE class=${rootCause::class.simpleName} " +
                        "message=${rootCause.message} HTTP=${httpCode ?: "none"}"
                )
            }
        )
        return result
    }
}
