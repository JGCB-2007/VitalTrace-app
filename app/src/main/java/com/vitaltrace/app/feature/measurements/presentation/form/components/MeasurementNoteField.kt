package com.vitaltrace.app.feature.measurements.presentation.form.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun MeasurementNoteField(
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FieldLabel(text = stringResource(R.string.measurement_form_note))
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 112.dp),
            placeholder = {
                Text(
                    text = stringResource(R.string.measurement_form_note_placeholder),
                    color = Color(0xFFA5AFB5)
                )
            },
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = VitalTraceTeal,
                unfocusedBorderColor = Color(0xFFE1DDD3)
            )
        )
    }
}
