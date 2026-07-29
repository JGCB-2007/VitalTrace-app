package com.vitaltrace.app.feature.auth.presentation

sealed interface LoginUiEffect {

    data object NavigateToHome : LoginUiEffect
}