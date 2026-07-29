package com.vitaltrace.app.feature.splash.presentation

sealed interface SplashUiEffect {

    data object NavigateToLogin : SplashUiEffect

    data object NavigateToHome : SplashUiEffect
}