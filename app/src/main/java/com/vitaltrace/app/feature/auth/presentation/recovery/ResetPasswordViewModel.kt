package com.vitaltrace.app.feature.auth.presentation.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPassword: ResetPasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState = _uiState.asStateFlow()
    private val _effects = Channel<ResetPasswordEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEmailChange(value: String) = update { copy(email = value) }
    fun onTokenChange(value: String) = update { copy(token = value) }
    fun onPasswordChange(value: String) = update { copy(password = value) }
    fun onConfirmationChange(value: String) = update { copy(passwordConfirmation = value) }
    fun togglePasswordVisibility() = update { copy(isPasswordVisible = !isPasswordVisible) }
    fun toggleConfirmationVisibility() = update {
        copy(isConfirmationVisible = !isConfirmationVisible)
    }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            resetPassword(
                state.email,
                state.token,
                state.password,
                state.passwordConfirmation
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                _effects.send(ResetPasswordEffect.NavigateToLogin)
            }.onFailure { exception ->
                val message = when (exception.message) {
                    "Enter a valid email address." -> "Ingresa un correo electrónico válido."
                    "Token is required." -> "Ingresa el token recibido por correo."
                    "Passwords do not match." -> "Las contraseñas no coinciden."
                    "Password must contain at least 8 characters, letters, numbers and a symbol." ->
                        "Usa al menos 8 caracteres, letras, números y un símbolo."
                    else -> "No pudimos restablecer la contraseña. Verifica el token e inténtalo de nuevo."
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    private fun update(transform: ResetPasswordUiState.() -> ResetPasswordUiState) {
        _uiState.update { it.transform().copy(errorMessage = null) }
    }
}
