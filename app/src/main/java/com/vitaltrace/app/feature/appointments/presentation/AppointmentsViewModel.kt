package com.vitaltrace.app.feature.appointments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientAppointmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsViewModel @Inject constructor(
    private val getPatientAppointments: GetPatientAppointmentsUseCase,
    private val appointmentsMapper: AppointmentsMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState = _uiState.asStateFlow()

    private var appointmentsRequest: Job? = null

    init {
        loadAppointments()
    }

    fun retry() {
        loadAppointments()
    }

    fun showAppointmentDetail(appointmentId: Long) {
        val content = (_uiState.value.contentState as? AppointmentsContentState.Success)?.content
            ?: return
        val appointment = sequenceOf(content.nextAppointment)
            .plus(content.upcomingAppointments.asSequence())
            .plus(content.previousAppointments.asSequence())
            .filterNotNull()
            .firstOrNull { it.id == appointmentId }
            ?: return

        _uiState.update { it.copy(selectedAppointmentDetail = appointment.toDetail()) }
    }

    fun dismissAppointmentDetail() {
        _uiState.update { it.copy(selectedAppointmentDetail = null) }
    }

    private fun loadAppointments() {
        if (appointmentsRequest?.isActive == true) return

        _uiState.update {
            it.copy(
                contentState = AppointmentsContentState.Loading,
                selectedAppointmentDetail = null
            )
        }
        appointmentsRequest = viewModelScope.launch {
            getPatientAppointments()
                .onSuccess { page ->
                    _uiState.update {
                        it.copy(
                            contentState = AppointmentsContentState.Success(
                                appointmentsMapper.map(page)
                            )
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            contentState = AppointmentsContentState.Error(
                                "No pudimos cargar tus citas. Intenta de nuevo."
                            )
                        )
                    }
                }
        }
    }
}
