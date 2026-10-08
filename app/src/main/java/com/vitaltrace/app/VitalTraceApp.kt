package com.vitaltrace.app

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaltrace.app.core.settings.ThemePreference
import com.vitaltrace.app.feature.onboarding.OnboardingScreen
import com.vitaltrace.app.navigation.AppNavHost
import com.vitaltrace.app.ui.theme.VitalTraceTheme

@Composable
fun VitalTraceApp(viewModel: AppShellViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (state.preferences.theme) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
    val activity = requireNotNull(LocalActivity.current)

    DisposableEffect(state.preferences.secureScreenEnabled) {
        if (state.preferences.secureScreenEnabled) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onDispose { }
    }
    VitalTraceTheme(darkTheme = darkTheme, largeText = state.preferences.largeTextEnabled) {
        when {
            !state.loaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            !state.preferences.onboardingCompleted -> OnboardingScreen(viewModel::completeOnboarding)
            else -> AppNavHost()
        }
    }
}
