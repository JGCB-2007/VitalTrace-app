package com.vitaltrace.app.feature.home.presentation

data class HomeUiState(
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null
)