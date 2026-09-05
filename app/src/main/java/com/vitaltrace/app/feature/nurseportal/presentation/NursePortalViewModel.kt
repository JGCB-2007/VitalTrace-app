package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import com.vitaltrace.app.feature.nurseportal.data.repository.NursePortalException
import com.vitaltrace.app.feature.nurseportal.domain.model.NursePatient
import com.vitaltrace.app.feature.nurseportal.domain.repository.NurseRepository
import com.vitaltrace.app.feature.nurseportal.domain.selection.NursePatientSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NursePortalViewModel @Inject constructor(
    private val repository: NurseRepository,
    private val selection: NursePatientSelection,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val mutable = MutableStateFlow(NursePortalUiState())
    val state = mutable.asStateFlow()
    private val effectChannel = Channel<NursePortalEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    init { loadDashboard(); loadPatients() }

    fun selectSection(section: NurseSection) {
        mutable.update { it.copy(section = section, error = null) }
        when (section) { NurseSection.ALERTS -> loadAlerts(); NurseSection.APPOINTMENTS -> loadAppointments(); else -> Unit }
    }
    fun selectPatientSection(section: NursePatientSection) {
        mutable.update { it.copy(patientSection = section, error = null) }
        selectedPatientId()?.let { loadPatientSection(it, section) }
    }
    fun retry() { loadDashboard(); loadPatients(mutable.value.search) }
    fun search(value: String) { mutable.update { it.copy(search = value) }; loadPatients(value) }
    fun selectPatient(patient: NursePatient) {
        selection.select(patient.id)
        mutable.update { it.copy(selectedPatient = patient, section = NurseSection.PATIENTS, patientSection = NursePatientSection.SUMMARY, loading = true, error = null) }
        loadPatient(patient.id)
    }
    fun clearPatient() { selection.clear(); mutable.update { it.copy(selectedPatient = null, profile = null, patientSummary = null, measurements = emptyList(), patientAppointments = emptyList(), diagnoses = emptyList(), treatments = emptyList(), history = null, patientAlerts = emptyList(), patientAlertsError = null) } }
    fun refresh() { loadDashboard(); loadPatients(mutable.value.search); selectedPatientId()?.let(::loadPatient) }
    fun retryAppointments() = loadAppointments()
    fun retryAlerts() = loadAlerts()
    fun retryPatientAlerts() { selectedPatientId()?.let { loadPatientSection(it, NursePatientSection.ALERTS) } }
    fun loadAppointment(id: Long) = viewModelScope.launch { repository.getAppointmentDetail(id).onSuccess { appointment -> mutable.update { it.copy(selectedAppointment = appointment) } }.onFailure(::handleFailure) }
    fun loadAlert(id: Long) = viewModelScope.launch { repository.getAlertDetail(id).onSuccess { alert -> mutable.update { it.copy(selectedAlert = alert) } }.onFailure(::handleFailure) }
    fun dismissAppointment() { mutable.update { it.copy(selectedAppointment = null) } }
    fun dismissAlert() { mutable.update { it.copy(selectedAlert = null) } }
    fun classify(id: Long, comment: String?) = mutateAlert { repository.classifyAlert(id, comment) }
    fun escalate(id: Long, comment: String?) = mutateAlert { repository.escalateAlert(id, comment) }
    fun createMeasurement(typeId: Long, value: Double, unit: String, measuredAt: String, observation: String?) = viewModelScope.launch {
        val patientId = selectedPatientId() ?: return@launch
        repository.createMeasurement(patientId, typeId, value, unit, measuredAt, observation)
            .onSuccess { mutable.update { it.copy(mutationMessage = "Medición registrada correctamente.") }; refresh() }
            .onFailure(::handleFailure)
    }
    fun clearMessage() { mutable.update { it.copy(mutationMessage = null) } }
    fun logout() = viewModelScope.launch { logoutUseCase(); selection.clear(); effectChannel.send(NursePortalEffect.NavigateToLogin) }

    private fun loadDashboard() = viewModelScope.launch {
        repository.getDashboard().onSuccess { summary -> mutable.update { it.copy(summary = summary, loading = false, error = null) } }.onFailure(::handleFailure)
    }
    private fun loadPatients(search: String? = mutable.value.search) = viewModelScope.launch {
        repository.getAssignedPatients(search?.takeIf(String::isNotBlank)).onSuccess { page ->
            mutable.update { it.copy(patients = page.items, loading = false, error = null) }
            restoreSelection(page.items)
        }.onFailure(::handleFailure)
    }
    private fun restoreSelection(patients: List<NursePatient>) {
        val saved = selection.selectedId.value
        val patient = patients.firstOrNull { it.id == saved } ?: patients.singleOrNull()
        if (patient != null) { if (saved != patient.id) selection.select(patient.id); mutable.update { it.copy(selectedPatient = patient) }; loadPatient(patient.id) }
    }
    private fun loadPatient(id: Long) = viewModelScope.launch {
        mutable.update { it.copy(loading = true) }
        val profile = repository.getPatientProfile(id)
        val summary = repository.getPatientSummary(id)
        val measurements = repository.getMeasurements(id)
        val appointments = repository.getPatientAppointments(id)
        val diagnoses = repository.getDiagnoses(id)
        val treatments = repository.getTreatments(id)
        val history = repository.getClinicalHistory(id)
        val alerts = repository.getPatientAlerts(id)
        val results = listOf(profile, summary, measurements, appointments, diagnoses, treatments, history, alerts)
        val failure = results.firstOrNull { it.isFailure }?.exceptionOrNull()
        if (failure != null) { handleFailure(failure); return@launch }
        mutable.update { it.copy(loading = false, profile = profile.getOrNull(), patientSummary = summary.getOrNull(), measurements = measurements.getOrNull()?.items.orEmpty(), patientAppointments = appointments.getOrNull()?.items.orEmpty(), diagnoses = diagnoses.getOrNull()?.items.orEmpty(), treatments = treatments.getOrNull()?.items.orEmpty(), history = history.getOrNull(), patientAlerts = alerts.getOrNull()?.items.orEmpty()) }
        repository.getMeasurementTypes().onSuccess { types -> mutable.update { it.copy(measurementTypes = types) } }
    }
    private fun loadPatientSection(id: Long, section: NursePatientSection) = viewModelScope.launch {
        when (section) {
            NursePatientSection.MEASUREMENTS -> repository.getMeasurements(id).onSuccess { page -> mutable.update { it.copy(measurements = page.items) } }.onFailure(::handleFailure)
            NursePatientSection.DIAGNOSES -> repository.getDiagnoses(id).onSuccess { page -> mutable.update { it.copy(diagnoses = page.items) } }.onFailure(::handleFailure)
            NursePatientSection.TREATMENTS -> repository.getTreatments(id).onSuccess { page -> mutable.update { it.copy(treatments = page.items) } }.onFailure(::handleFailure)
            NursePatientSection.HISTORY -> repository.getClinicalHistory(id).onSuccess { history -> mutable.update { it.copy(history = history) } }.onFailure(::handleFailure)
            NursePatientSection.ALERTS -> repository.getPatientAlerts(id).onSuccess { page -> mutable.update { it.copy(patientAlerts = page.items, patientAlertsError = null) } }.onFailure(::handlePatientAlertsFailure)
            else -> Unit
        }
    }
    private fun loadAppointments() = viewModelScope.launch {
        repository.getAppointments().onSuccess { page -> mutable.update { it.copy(nurseAppointments = page.items, appointmentsError = null, loading = false) } }.onFailure(::handleAppointmentsFailure)
    }
    private fun loadAlerts() = viewModelScope.launch {
        repository.getAlerts().onSuccess { page -> mutable.update { it.copy(nurseAlerts = page.items, alertsError = null, loading = false) } }.onFailure(::handleAlertsFailure)
    }
    private fun mutateAlert(block: suspend () -> Result<com.vitaltrace.app.feature.nurseportal.domain.model.NurseAlert>) = viewModelScope.launch {
        val section = mutable.value.section
        val patientSection = mutable.value.patientSection
        val patientId = selectedPatientId()
        block().onSuccess { alert ->
            mutable.update { it.copy(selectedAlert = alert, mutationMessage = "Alerta actualizada correctamente.") }
            when {
                section == NurseSection.ALERTS -> loadAlerts()
                section == NurseSection.PATIENTS && patientSection == NursePatientSection.ALERTS && patientId != null -> loadPatientSection(patientId, NursePatientSection.ALERTS)
            }
        }.onFailure(::handleFailure)
    }
    private fun selectedPatientId() = selection.selectedId.value ?: mutable.value.selectedPatient?.id
    private fun handleAppointmentsFailure(error: Throwable) {
        when ((error as? NursePortalException)?.httpCode) {
            401 -> viewModelScope.launch { logoutUseCase(); selection.clear(); effectChannel.send(NursePortalEffect.NavigateToLogin) }
            else -> mutable.update { it.copy(loading = false, appointmentsError = error.message ?: "No pudimos cargar las citas.") }
        }
    }
    private fun handleAlertsFailure(error: Throwable) {
        when ((error as? NursePortalException)?.httpCode) {
            401 -> viewModelScope.launch { logoutUseCase(); selection.clear(); effectChannel.send(NursePortalEffect.NavigateToLogin) }
            else -> mutable.update { it.copy(loading = false, alertsError = error.message ?: "No pudimos cargar las alertas.") }
        }
    }
    private fun handlePatientAlertsFailure(error: Throwable) {
        when ((error as? NursePortalException)?.httpCode) {
            401 -> viewModelScope.launch { logoutUseCase(); selection.clear(); effectChannel.send(NursePortalEffect.NavigateToLogin) }
            403 -> { selection.clear(); mutable.update { it.copy(selectedPatient = null, error = "No tienes autorización para consultar este paciente.", section = NurseSection.PATIENTS) } }
            else -> mutable.update { it.copy(patientAlertsError = error.message ?: "No pudimos cargar las alertas del paciente.") }
        }
    }
    private fun handleFailure(error: Throwable) {
        when ((error as? NursePortalException)?.httpCode) {
            401 -> viewModelScope.launch { logoutUseCase(); selection.clear(); effectChannel.send(NursePortalEffect.NavigateToLogin) }
            403 -> { selection.clear(); mutable.update { it.copy(selectedPatient = null, error = "No tienes autorización para consultar este paciente.", section = NurseSection.PATIENTS) } }
            else -> mutable.update { it.copy(loading = false, error = error.message ?: "No pudimos cargar la información.") }
        }
    }
}