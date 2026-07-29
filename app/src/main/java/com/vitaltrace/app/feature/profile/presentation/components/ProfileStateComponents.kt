package com.vitaltrace.app.feature.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun ProfileLoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = VitalTraceTeal)
        Text(
            text = stringResource(R.string.profile_loading),
            modifier = Modifier.padding(top = 16.dp),
            color = VitalTraceNavy
        )
    }
}

@Composable
fun ProfileErrorState(
    message: String?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message?.takeIf(String::isNotBlank)
                ?: stringResource(R.string.profile_error),
            color = VitalTraceNavy,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetryClick,
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VitalTraceNavy)
        ) {
            Text(text = stringResource(R.string.profile_retry))
        }
    }
}
