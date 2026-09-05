package com.vitaltrace.app.feature.splash.presentation

sealed interface SplashUiEffect {
    data object NavigateToLogin : SplashUiEffect
    data object NavigateToHome : SplashUiEffect
    data object NavigateToRelativePortal : SplashUiEffect
    data object NavigateToNursePortal : SplashUiEffect
    data object NavigateToPortalSelector : SplashUiEffect
}