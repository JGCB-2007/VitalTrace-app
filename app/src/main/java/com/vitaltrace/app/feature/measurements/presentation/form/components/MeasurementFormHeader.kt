package com.vitaltrace.app.feature.measurements.presentation.form.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
fun MeasurementFormHeader(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.measurement_form_back),
                    tint = VitalTraceNavy
                )
            }
        }
        Column(modifier = Modifier.padding(start = 18.dp)) {
            Text(
                text = stringResource(R.string.measurement_form_title),
                color = VitalTraceNavy,
                fontFamily = FontFamily.Serif,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.measurement_form_subtitle),
                color = Color(0xFF53636D),
                fontSize = 18.sp
            )
        }
    }
}
