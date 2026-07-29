package com.vitaltrace.app.feature.measurements.presentation.form

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

class MeasurementFormViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MeasurementFormUiState())
    val uiState = _uiState.asStateFlow()

    private val effectChannel = Channel<MeasurementFormUiEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    fun updateSystolic(value: String) {
        _uiState.update { state ->
            state.copy(
                systolic = value.filter(Char::isDigit).take(3),
                systolicError = null
            )
        }
    }

    fun updateDiastolic(value: String) {
        _uiState.update { state ->
            state.copy(
                diastolic = value.filter(Char::isDigit).take(3),
                diastolicError = null
            )
        }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { state -> state.copy(date = date) }
    }

    fun updateTime(time: LocalTime) {
        _uiState.update { state -> state.copy(time = time) }
    }

    fun updateNote(note: String) {
        _uiState.update { state -> state.copy(note = note.take(240)) }
    }

    fun saveMeasurement() {
        val state = _uiState.value
        if (state.isSaving) return

        val systolicError = validatePressureValue(state.systolic)
        val diastolicError = validatePressureValue(state.diastolic)
        if (systolicError != null || diastolicError != null) {
            _uiState.update {
                it.copy(
                    systolicError = systolicError,
                    diastolicError = diastolicError
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true) }
        effectChannel.trySend(MeasurementFormUiEffect.MeasurementSaved)
        _uiState.update { it.copy(isSaving = false) }
    }

    private fun validatePressureValue(value: String): MeasurementFieldError? {
        if (value.isBlank()) return MeasurementFieldError.REQUIRED
        return if (value.toIntOrNull() in 1..999) null else MeasurementFieldError.INVALID
    }
}
