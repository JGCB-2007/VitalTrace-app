package com.vitaltrace.app.feature.measurements.presentation.form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementFieldError
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementTypeOption
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementValueFields(
    availableTypes: List<MeasurementTypeOption>,
    selectedType: MeasurementTypeOption?,
    value: String,
    typeError: MeasurementFieldError?,
    valueError: MeasurementFieldError?,
    onTypeSelected: (Long) -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Column {
            FieldLabel(text = stringResource(R.string.measurement_form_type))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedType?.name.orEmpty(),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (typeError == null) 82.dp else 106.dp)
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    isError = typeError != null,
                    readOnly = true,
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = measurementFieldColors()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    availableTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                onTypeSelected(type.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                FieldLabel(text = stringResource(R.string.measurement_form_value))
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (valueError == null) 82.dp else 106.dp),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Serif,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = valueError != null,
                    supportingText = valueError?.let { error ->
                        {
                            Text(
                                when (error) {
                                    MeasurementFieldError.REQUIRED -> stringResource(R.string.measurement_form_required_error)
                                    MeasurementFieldError.INVALID -> stringResource(R.string.measurement_form_invalid_error)
                                    MeasurementFieldError.OUT_OF_RANGE -> stringResource(
                                        R.string.measurement_form_range_error,
                                        selectedType?.minimumValue?.toInt() ?: 0,
                                        selectedType?.maximumValue?.toInt() ?: 0
                                    )
                                }
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = measurementFieldColors()
                )
            }
            Column(modifier = Modifier.width(100.dp)) {
                FieldLabel(text = stringResource(R.string.measurement_form_unit))
                Surface(
                    modifier = Modifier.fillMaxWidth().height(82.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = selectedType?.unit.orEmpty(),
                        modifier = Modifier.padding(top = 27.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        selectedType?.let { type ->
            val quickValues = when (type.id) {
                1L -> listOf("110", "120", "130")
                2L -> listOf("90", "110", "140")
                3L -> listOf("95", "97", "99")
                else -> emptyList()
            }
            if (quickValues.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickValues.forEach { quick ->
                        AssistChip(onClick = { onValueChange(quick) }, label = { Text(quick) })
                    }
                }
            }
            val numericValue = value.toDoubleOrNull()
            val healthy = when (type.id) {
                1L -> 90.0..140.0
                2L -> 70.0..180.0
                3L -> 92.0..100.0
                else -> null
            }
            if (numericValue != null && healthy != null && numericValue !in healthy) {
                Text(
                    "Este valor está fuera del rango general de referencia. VitalTrace no realiza diagnósticos; si presentas síntomas, contacta a un profesional.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun measurementFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.secondary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    errorContainerColor = MaterialTheme.colorScheme.surface
)

@Composable
internal fun FieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}
