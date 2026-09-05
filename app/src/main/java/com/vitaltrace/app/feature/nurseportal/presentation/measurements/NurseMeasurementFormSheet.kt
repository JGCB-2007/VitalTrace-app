package com.vitaltrace.app.feature.nurseportal.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementFormActions
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementInformationCard
import com.vitaltrace.app.feature.measurements.presentation.form.components.MeasurementNoteField
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
    var selected by remember(state.measurementTypes) { mutableStateOf(state.measurementTypes.firstOrNull { it.active }) }
    var expanded by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }
    var observation by remember { mutableStateOf("") }
    var invalid by remember { mutableStateOf(false) }
    val measuredAt = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VitalTraceWarmBackground,
        scrimColor = Color(0x990C1C29),
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(start = 24.dp, end = 24.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Registrar medición", color = VitalTraceNavy, fontFamily = FontFamily.Serif, fontSize = 27.sp, fontWeight = FontWeight.Bold)
            Text(state.selectedPatient?.fullName.orEmpty(), color = VitalTraceTeal, fontWeight = FontWeight.Bold)
            MeasurementInformationCard()
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selected?.name.orEmpty(),
                    onValueChange = {},
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Tipo de medición") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    shape = RoundedCornerShape(18.dp),
                    colors = nurseFieldColors()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    state.measurementTypes.filter { it.active }.forEach { type ->
                        DropdownMenuItem(text = { Text("${type.name} · ${type.unit}") }, onClick = { selected = type; expanded = false })
                    }
                }
            }
            OutlinedTextField(
                value = value,
                onValueChange = { value = it; invalid = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Valor") },
                suffix = { Text(selected?.unit.orEmpty()) },
                isError = invalid,
                supportingText = { if (invalid) Text("Ingresa un valor numérico válido.") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = nurseFieldColors()
            )
            OutlinedTextField(
                value = measuredAt,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text("Fecha y hora") },
                shape = RoundedCornerShape(18.dp),
                colors = nurseFieldColors()
            )
            MeasurementNoteField(observation, { observation = it })
            MeasurementFormActions(
                isSaving = false,
                onSaveClick = {
                    val number = value.toDoubleOrNull()
                    val type = selected
                    if (number == null || type == null) invalid = true
                    else onSubmit(type.id, number, type.unit, measuredAt, observation.takeIf(String::isNotBlank))
                },
                onCancelClick = onDismiss
            )
        }
    }
}

@Composable
private fun nurseFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = VitalTraceTeal,
    unfocusedBorderColor = Color(0xFFE1DDD3)
)