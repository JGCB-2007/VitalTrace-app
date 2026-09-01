package com.vitaltrace.app.feature.splash.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vitaltrace.app.feature.splash.presentation.components.SplashBackground
import com.vitaltrace.app.feature.splash.presentation.components.SplashBrand
import com.vitaltrace.app.feature.splash.presentation.components.SplashLoadingIndicator
import com.vitaltrace.app.ui.theme.VitalTraceTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRelativePortal: () -> Unit = onNavigateToHome,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                SplashUiEffect.NavigateToLogin -> onNavigateToLogin()
                SplashUiEffect.NavigateToHome -> onNavigateToHome()
                SplashUiEffect.NavigateToRelativePortal -> onNavigateToRelativePortal()
            }
        }
    }

    SplashContent()
}

@Composable
private fun SplashContent() {
    var animationStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationStarted = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "splashContentAlpha"
    )
    val contentScale by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.92f,
        animationSpec = tween(durationMillis = 1900),
        label = "splashContentScale"
    )

    SplashBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            SplashBrand(
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer {
                        alpha = contentAlpha
                        scaleX = contentScale
                        scaleY = contentScale
                    }
            )
            SplashLoadingIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer { alpha = contentAlpha }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SplashScreenPreview() {
    VitalTraceTheme(dynamicColor = false) {
        SplashContent()
    }
}
