package com.vitaltrace.app.feature.auth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.R
import com.vitaltrace.app.ui.theme.VitalTraceNavy

@Composable
fun LoginBrandHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0A5968),
                        VitalTraceNavy,
                        Color(0xFF006B70)
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                )
            )
            .padding(
                start = 32.dp,
                top = 64.dp,
                end = 32.dp,
                bottom = 28.dp
            )
    ) {
        Image(
            painter = painterResource(R.drawable.vitaltrace_logo),
            contentDescription = stringResource(R.string.login_logo_description),
            modifier = Modifier
                .width(220.dp)
                .height(64.dp),
            alignment = Alignment.CenterStart,
            contentScale = ContentScale.Crop,
            colorFilter = null,
            alpha = 1f
        )

        Text(
            text = stringResource(R.string.login_welcome_title),
            color = Color.White,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(top = 42.dp)
        )

        Text(
            text = stringResource(R.string.login_welcome_description),
            color = Color.White.copy(alpha = 0.84f),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
