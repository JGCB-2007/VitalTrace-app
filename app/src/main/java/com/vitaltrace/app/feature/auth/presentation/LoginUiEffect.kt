package com.vitaltrace.app.feature.auth.presentation

sealed interface LoginUiEffect {
    data object NavigateToHome : LoginUiEffect
    data object NavigateToRelativePortal : LoginUiEffect
    data object NavigateToNursePortal : LoginUiEffect
    data object NavigateToPortalSelector : LoginUiEffect
}