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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.measurements.presentation.form.MeasurementFieldError
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun BloodPressureFields(
    systolic: String,
    diastolic: String,
    systolicError: MeasurementFieldError?,
    diastolicError: MeasurementFieldError?,
    onSystolicChange: (String) -> Unit,
    onDiastolicChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        PressureField(
            label = stringResource(R.string.measurement_form_systolic),
            value = systolic,
            error = systolicError,
            onValueChange = onSystolicChange,
            modifier = Modifier.weight(1f)
        )
        PressureField(
            label = stringResource(R.string.measurement_form_diastolic),
            value = diastolic,
            error = diastolicError,
            onValueChange = onDiastolicChange,
            modifier = Modifier.weight(1f)
        )
        Column(modifier = Modifier.width(76.dp)) {
            FieldLabel(text = stringResource(R.string.measurement_form_unit))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp),
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1DDD3))
            ) {
                Text(
                    text = stringResource(R.string.measurement_form_pressure_unit),
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

@Composable
private fun PressureField(
    label: String,
    value: String,
    error: MeasurementFieldError?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FieldLabel(text = label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (error == null) 82.dp else 106.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = error != null,
            supportingText = error?.let {
                {
                    Text(
                        text = stringResource(it.messageResource()),
                        maxLines = 1
                    )
                }
            },
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = VitalTraceTeal,
                unfocusedBorderColor = Color(0xFFE1DDD3),
                errorContainerColor = Color.White
            )
        )
    }
}

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

private fun MeasurementFieldError.messageResource(): Int {
    return when (this) {
        MeasurementFieldError.REQUIRED -> R.string.measurement_form_required_error
        MeasurementFieldError.INVALID -> R.string.measurement_form_invalid_error
    }
}
