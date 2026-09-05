package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.core.presentation.localization.SpanishDateTime
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementFieldError
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementTypeOption
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementFormActions
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementInformationCard
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementNoteField
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementValueFields
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseMeasurementType
import com.vitaltrace.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NurseMeasurementFormSheet(
    state: NursePortalUiState,
    onDismiss: () -> Unit,
    onSubmit: (Long, Double, String, String, String?) -> Unit
) {
    var selectedTypeId by remember(state.measurementTypes) {
        mutableStateOf(state.measurementTypes.firstOrNull { it.active }?.id)
    }
    val selectedType = state.measurementTypes.firstOrNull { it.id == selectedTypeId }
    var value by remember { mutableStateOf("") }
    var observation by remember { mutableStateOf("") }
    var typeError by remember { mutableStateOf<MeasurementFieldError?>(null) }
    var valueError by remember { mutableStateOf<MeasurementFieldError?>(null) }
    // `measuredAt` stays in the API wire format (sent verbatim in the payload);
    // `measuredAtDisplay` is the es-NI rendering shown in the read-only field.
    val measuredAt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) }
    val measuredAtDisplay = remember(measuredAt) {
        val (date, time) = SpanishDateTime.formatApiDateTime(measuredAt)
        if (time.isBlank()) date else "$date · $time"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VitalTraceWarmBackground,
        scrimColor = Color(0x990C1C29),
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.measurement_form_title),
                    color = VitalTraceNavy,
                    fontFamily = FontFamily.Serif,
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.selectedPatient?.fullName.orEmpty(),
                    color = VitalTraceTeal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            MeasurementInformationCard()
            MeasurementValueFields(
                availableTypes = state.measurementTypes.filter { it.active }.map { it.toMeasurementTypeOption() },
                selectedType = selectedType?.toMeasurementTypeOption(),
                value = value,
                typeError = typeError,
                valueError = valueError,
                onTypeSelected = { id ->
                    selectedTypeId = id
                    typeError = null
                },
                onValueChange = {
                    value = it
                    valueError = null
                }
            )
            NurseMeasuredAtField(measuredAtDisplay)
            MeasurementNoteField(observation, { observation = it })
            val errorMessage = when {
                typeError != null -> stringResource(R.string.nurse_measurement_form_type_required)
                valueError != null -> stringResource(R.string.measurement_form_invalid_error)
                else -> null
            }
            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            MeasurementFormActions(
                isSaving = false,
                onSaveClick = {
                    val number = value.toDoubleOrNull()
                    val type = selectedType
                    typeError = if (type == null) MeasurementFieldError.REQUIRED else null
                    valueError = if (number == null) MeasurementFieldError.INVALID else null
                    if (type != null && number != null) {
                        onSubmit(type.id, number, type.unit, measuredAt, observation.takeIf(String::isNotBlank))
                    }
                },
                onCancelClick = onDismiss
            )
        }
    }
}

@Composable
private fun NurseMeasuredAtField(text: String) {
    Column {
        com.vitaltrace.app.feature.measurements.presentation.form.components.FieldLabel(
            text = stringResource(R.string.nurse_measurement_form_datetime_label)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFFE1DDD3))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Rounded.Schedule, contentDescription = null, tint = Color(0xFF657078))
                Text(
                    text = text,
                    color = VitalTraceNavy,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    maxLines = 1
                )
            }
        }
    }
}

private fun NurseMeasurementType.toMeasurementTypeOption() = MeasurementTypeOption(
    id = id,
    name = name,
    unit = unit,
    decimals = decimals
)
