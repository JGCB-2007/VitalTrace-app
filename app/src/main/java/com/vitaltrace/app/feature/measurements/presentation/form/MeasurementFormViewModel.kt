package com.vitaltrace.app.feature.measurements.presentation.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.patient.domain.usecase.CreatePatientMeasurementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MeasurementFormViewModel @Inject constructor(
    private val createPatientMeasurement: CreatePatientMeasurementUseCase
) : ViewModel() {
    private val localMeasurementTypes = LocalMeasurementTypeCatalog.types
    private val _uiState = MutableStateFlow(initialState())
    val uiState = _uiState.asStateFlow()
    private val effectChannel = Channel<MeasurementFormUiEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()
    private var saveRequest: Job? = null

    fun selectType(id: Long) {
        _uiState.update { it.copy(selectedTypeId = id, typeError = null, errorMessage = null) }
    }

    fun updateValue(value: String) {
        _uiState.update {
            it.copy(
                value = value.filter { character -> character.isDigit() || character == '.' },
                valueError = null,
                errorMessage = null
            )
        }
    }

    fun updateDate(date: LocalDate) = _uiState.update { it.copy(date = date) }
    fun updateTime(time: LocalTime) = _uiState.update { it.copy(time = time) }
    fun updateNote(note: String) = _uiState.update { it.copy(note = note.take(240)) }

    fun saveMeasurement() {
        if (saveRequest?.isActive == true) return
        val state = _uiState.value
        val selectedType = state.selectedType
        val numericValue = state.value.toDoubleOrNull()
        val typeError = if (selectedType == null) MeasurementFieldError.REQUIRED else null
        val valueError = when {
            state.value.isBlank() -> MeasurementFieldError.REQUIRED
            numericValue == null -> MeasurementFieldError.INVALID
            else -> null
        }
        if (typeError != null || valueError != null) {
            _uiState.update { it.copy(typeError = typeError, valueError = valueError) }
            return
        }

        saveRequest = viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            createPatientMeasurement(
                measurementTypeId = requireNotNull(selectedType).id,
                value = requireNotNull(numericValue),
                unit = selectedType.unit,
                measuredAt = LocalDateTime.of(state.date, state.time)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                observation = state.note.trim().ifBlank { null }
            ).onSuccess {
                _uiState.value = initialState()
                effectChannel.send(MeasurementFormUiEffect.MeasurementSaved)
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "No pudimos guardar la medición. Intenta de nuevo."
                    )
                }
            }
        }
    }

    private fun initialState() = MeasurementFormUiState(
        availableTypes = localMeasurementTypes,
        selectedTypeId = localMeasurementTypes.firstOrNull()?.id,
        isLoadingTypes = false
    )
}
