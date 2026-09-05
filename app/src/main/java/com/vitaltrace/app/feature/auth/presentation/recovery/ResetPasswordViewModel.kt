package com.vitaltrace.app.feature.auth.presentation.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.core.presentation.localization.AuthMessagesEs
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
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
                // Upstream (use case + repository) already emits localized Spanish
                // text; anything else falls back to a neutral reset message.
                val message = (exception as? AuthException)?.message?.takeIf(String::isNotBlank)
                    ?: AuthMessagesEs.RESET_FAILED
                _uiState.update { it.copy(isLoading = false, errorMessage = message) }
            }
        }
    }

    private fun update(transform: ResetPasswordUiState.() -> ResetPasswordUiState) {
        _uiState.update { it.transform().copy(errorMessage = null) }
    }
}
