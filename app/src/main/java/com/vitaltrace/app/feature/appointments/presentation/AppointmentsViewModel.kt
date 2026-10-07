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

    fun refresh() {
        appointmentsRequest?.cancel()
        appointmentsRequest = null
        loadAppointments(refresh = true)
    }

    fun loadMore() {
        val content = (_uiState.value.contentState as? AppointmentsContentState.Success)?.content ?: return
        if (_uiState.value.isLoadingMore || content.currentPage >= content.lastPage) return
        loadAppointments(page = content.currentPage + 1)
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

    private fun loadAppointments(page: Int = 1, refresh: Boolean = false) {
        if (appointmentsRequest?.isActive == true) return

        _uiState.update { state ->
            when {
                refresh -> state.copy(isRefreshing = true, selectedAppointmentDetail = null)
                page > 1 -> state.copy(isLoadingMore = true)
                else -> state.copy(contentState = AppointmentsContentState.Loading, selectedAppointmentDetail = null)
            }
        }
        appointmentsRequest = viewModelScope.launch {
            getPatientAppointments(page, forceRefresh = refresh)
                .onSuccess { page ->
                    _uiState.update {
                        val mapped = appointmentsMapper.map(page)
                        val previous = (it.contentState as? AppointmentsContentState.Success)?.content
                        val content = if (page.meta.currentPage > 1 && previous != null) {
                            previous.mergeWith(mapped).copy(
                                currentPage = page.meta.currentPage,
                                lastPage = page.meta.lastPage
                            )
                        } else mapped
                        it.copy(
                            contentState = AppointmentsContentState.Success(content),
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        if (page > 1 || refresh) it.copy(isRefreshing = false, isLoadingMore = false)
                        else it.copy(
                            contentState = AppointmentsContentState.Error("No pudimos cargar tus citas. Intenta de nuevo."),
                            isRefreshing = false,
                            isLoadingMore = false
                        )
                    }
                }
        }
    }

    fun updateQuery(value: String) = _uiState.update { it.copy(query = value.take(80)) }

    private fun AppointmentsContentUiModel.mergeWith(other: AppointmentsContentUiModel): AppointmentsContentUiModel =
        copy(
            nextAppointment = nextAppointment ?: other.nextAppointment,
            upcomingAppointments = (upcomingAppointments + listOfNotNull(other.nextAppointment) + other.upcomingAppointments)
                .filterNot { it.id == nextAppointment?.id }
                .distinctBy(AppointmentUiModel::id)
                .sortedBy(AppointmentUiModel::scheduledAt),
            previousAppointments = (previousAppointments + other.previousAppointments)
                .distinctBy(AppointmentUiModel::id)
        )
}
