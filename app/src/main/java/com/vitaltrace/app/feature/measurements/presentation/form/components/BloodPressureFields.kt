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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementFieldError
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementTypeOption
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
            FieldLabel(text = "Tipo de medición")
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedType?.name.orEmpty(),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (typeError == null) 82.dp else 106.dp)
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    textStyle = TextStyle(
                        color = VitalTraceNavy,
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
                FieldLabel(text = "Valor")
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (valueError == null) 82.dp else 106.dp),
                    textStyle = TextStyle(
                        color = VitalTraceNavy,
                        fontFamily = FontFamily.Serif,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = valueError != null,
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = measurementFieldColors()
                )
            }
            Column(modifier = Modifier.width(100.dp)) {
                FieldLabel(text = "Unidad")
                Surface(
                    modifier = Modifier.fillMaxWidth().height(82.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1DDD3))
                ) {
                    Text(
                        text = selectedType?.unit.orEmpty(),
                        modifier = Modifier.padding(top = 27.dp),
                        color = VitalTraceNavy,
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun measurementFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedBorderColor = VitalTraceTeal,
    unfocusedBorderColor = Color(0xFFE1DDD3),
    errorContainerColor = Color.White
)

@Composable
internal fun FieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 8.dp),
        color = Color(0xFF172C3A),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}
