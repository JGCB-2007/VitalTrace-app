package com.vitaltrace.app.feature.home.presentation

sealed interface HomeUiEffect {

    data object NavigateToLogin : HomeUiEffect
}