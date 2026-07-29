package com.vitaltrace.app.feature.splash.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.vitaltrace.app.ui.theme.VitalTraceMint
import com.vitaltrace.app.ui.theme.VitalTraceNavy
import com.vitaltrace.app.ui.theme.VitalTraceTeal
import com.vitaltrace.app.ui.theme.VitalTraceWarmBackground

@Composable
fun SplashBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        VitalTraceWarmBackground,
                        VitalTraceMint.copy(alpha = 0.20f),
                        VitalTraceWarmBackground
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-92).dp, y = (-74).dp)
                .size(250.dp)
                .clip(CircleShape)
                .background(VitalTraceTeal.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .offset(x = 96.dp, y = 82.dp)
                .size(280.dp)
                .clip(CircleShape)
                .background(VitalTraceNavy.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.CenterEnd)
                .offset(x = 54.dp, y = (-126).dp)
                .size(112.dp)
                .clip(CircleShape)
                .background(VitalTraceMint.copy(alpha = 0.14f))
        )
        content()
    }
}
