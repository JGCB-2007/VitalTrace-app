package com.vitaltrace.app.feature.relatives.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal

@Composable
fun RelativesLoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            CircularProgressIndicator(color = VitalTraceNavy, strokeWidth = 3.dp)
            Text(
                stringResource(R.string.relatives_loading),
                color = Color(0xFF5C6870)
            )
        }
    }
}

@Composable
fun RelativesEmptyState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StateIcon(
                background = Color(0xFFD9F2F0),
                content = VitalTraceTeal
            ) {
                Icon(
                    Icons.Rounded.Group,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp)
                )
            }
            Text(
                stringResource(R.string.relatives_empty_title),
                color = VitalTraceNavy,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(R.string.relatives_empty),
                color = Color(0xFF5C6870),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RelativesErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StateIcon(
                background = Color(0xFFF5DDDF),
                content = Color(0xFF8E3E45)
            ) {
                Icon(
                    Icons.Rounded.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.size(34.dp)
                )
            }
            Text(
                stringResource(R.string.relatives_error_title),
                color = VitalTraceNavy,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            Text(message, color = Color(0xFF5C6870), textAlign = TextAlign.Center)
            Button(onClick = onRetry) {
                Icon(Icons.Rounded.Refresh, contentDescription = null)
                Text(
                    stringResource(R.string.relatives_retry),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StateIcon(
    background: Color,
    content: Color,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.material3.LocalContentColor provides content,
            content = icon
        )
    }
}
