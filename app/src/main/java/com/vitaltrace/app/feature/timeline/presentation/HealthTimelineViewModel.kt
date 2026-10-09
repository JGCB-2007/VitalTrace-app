package com.vitaltrace.app.feature.timeline.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.core.presentation.formatClinicalDate
import com.vitaltrace.app.core.presentation.formatClinicalDateTime
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientAppointmentsUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientMeasurementsUseCase
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientTreatmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HealthTimelineViewModel @Inject constructor(
    private val getAppointments: GetPatientAppointmentsUseCase,
    private val getMeasurements: GetPatientMeasurementsUseCase,
    private val getTreatments: GetPatientTreatmentsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HealthTimelineUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun refresh() = load(refresh = true)
    fun retry() = load()
    fun updateQuery(value: String) = _uiState.update { it.copy(query = value) }
    fun selectType(type: TimelineEventType) = _uiState.update { it.copy(selectedType = type) }

    private fun load(refresh: Boolean = false) = viewModelScope.launch {
        _uiState.update {
            if (refresh) it.copy(refreshing = true, error = null)
            else it.copy(loading = true, error = null)
        }
        val appointments = async { getAppointments(forceRefresh = refresh) }.await()
        val measurements = async { getMeasurements(forceRefresh = refresh) }.await()
        val treatments = async { getTreatments(forceRefresh = refresh) }.await()
        val events = buildList {
            appointments.getOrNull()?.items?.forEach { add(it.toTimelineEvent()) }
            measurements.getOrNull()?.items?.forEach { add(it.toTimelineEvent()) }
            treatments.getOrNull()?.items?.forEach { add(it.toTimelineEvent()) }
        }.sortedByDescending(TimelineEventUiModel::sortValue)
        val allFailed = appointments.isFailure && measurements.isFailure && treatments.isFailure
        _uiState.update {
            it.copy(
                loading = false,
                refreshing = false,
                events = if (allFailed) it.events else events,
                error = if (allFailed) "No pudimos cargar tu línea de tiempo." else null
            )
        }
    }

    private fun Appointment.toTimelineEvent() = TimelineEventUiModel(
        id = "appointment-$id",
        type = TimelineEventType.APPOINTMENT,
        title = reason.ifBlank { "Cita médica" },
        subtitle = professional?.fullName.orEmpty().ifBlank { "Profesional de salud" },
        date = formatClinicalDateTime(scheduledAt),
        sortValue = scheduledAt,
        status = when (status.uppercase()) {
            "SCHEDULED" -> "Programada"; "CONFIRMED" -> "Confirmada"; "ATTENDED" -> "Realizada"
            "CANCELLED" -> "Cancelada"; "NO_SHOW" -> "No asistió"; else -> status
        }
    )

    private fun Measurement.toTimelineEvent() = TimelineEventUiModel(
        id = "measurement-$id",
        type = TimelineEventType.MEASUREMENT,
        title = measurementType?.name?.localizedName() ?: "Medición",
        subtitle = "$value $unit" + observation?.takeIf(String::isNotBlank)?.let { " · $it" }.orEmpty(),
        date = formatClinicalDateTime(measuredAt),
        sortValue = measuredAt,
        status = if (reviewStatus.uppercase() == "REVIEWED") "Revisada" else "Pendiente"
    )

    private fun Treatment.toTimelineEvent() = TimelineEventUiModel(
        id = "treatment-$id",
        type = TimelineEventType.TREATMENT,
        title = diagnosis?.description?.takeIf(String::isNotBlank) ?: "Tratamiento",
        subtitle = indications,
        date = formatClinicalDate(startDate),
        sortValue = startDate,
        status = when (status.uppercase()) {
            "ACTIVE" -> "Activo"; "FINISHED", "COMPLETED" -> "Finalizado"
            "SUSPENDED" -> "Suspendido"; else -> status
        }
    )

    private fun String.localizedName() = when (trim().lowercase()) {
        "systolic blood pressure" -> "Presión arterial"
        "blood glucose" -> "Glucosa en sangre"
        "oxygen saturation" -> "Saturación de oxígeno"
        else -> this
    }
}
