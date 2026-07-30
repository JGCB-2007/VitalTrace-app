package com.vitaltrace.app.feature.auth.presentation.recovery

data class ResetPasswordUiState(
    val email: String = "",
    val token: String = "",
    val password: String = "",
    val passwordConfirmation: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmationVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ResetPasswordEffect {
    data object NavigateToLogin : ResetPasswordEffect
}
