package com.vitaltrace.app.feature.auth.presentation.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.AccountActivationSession
import com.vitaltrace.app.feature.auth.domain.usecase.ResendActivationCodeUseCase
import com.vitaltrace.app.feature.auth.domain.usecase.SetInitialPasswordUseCase
import com.vitaltrace.app.feature.auth.domain.usecase.VerifyActivationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FirstAccessEmailUiState(val email: String = "", val error: String? = null)
data class ActivationCodeUiState(val email: String = "", val code: String = "", val isLoading: Boolean = false, val error: String? = null, val resendMessage: String? = null)
data class CreateInitialPasswordUiState(val password: String = "", val confirmation: String = "", val passwordVisible: Boolean = false, val confirmationVisible: Boolean = false, val isLoading: Boolean = false, val error: String? = null)

@HiltViewModel
class FirstAccessEmailViewModel @Inject constructor(private val session: AccountActivationSession) : ViewModel() {
    private val _state = MutableStateFlow(FirstAccessEmailUiState())
    val state = _state.asStateFlow()
    private val _continue = Channel<Unit>(Channel.BUFFERED)
    val continueFlow = _continue.receiveAsFlow()
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, error = null) }
    fun continueToCode() {
        val email = _state.value.email.trim().lowercase()
        if (!Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email)) {
            _state.update { it.copy(error = "Ingresa un correo válido.") }; return
        }
        session.begin(email); viewModelScope.launch { _continue.send(Unit) }
    }
}

@HiltViewModel
class ActivationCodeViewModel @Inject constructor(
    private val session: AccountActivationSession,
    private val verifyCode: VerifyActivationCodeUseCase,
    private val resendCode: ResendActivationCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ActivationCodeUiState(email = session.email))
    val state = _state.asStateFlow()
    private val _verified = Channel<Unit>(Channel.BUFFERED)
    val verified = _verified.receiveAsFlow()
    fun onCodeChange(value: String) = _state.update { it.copy(code = value.filter(Char::isDigit).take(6), error = null) }
    fun verify() = launchRequest {
        verifyCode(session.email, _state.value.code).onSuccess { token -> session.verified(token); _verified.send(Unit) }
    }
    fun resend() = launchRequest {
        resendCode(session.email).onSuccess { _state.update { it.copy(resendMessage = "Se envió un nuevo código.") } }
    }
    private fun launchRequest(block: suspend () -> Result<*>) {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, resendMessage = null) }
            block().onFailure { failure -> _state.update { it.copy(error = failure.message ?: "No pudimos completar la solicitud.") } }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

@HiltViewModel
class CreateInitialPasswordViewModel @Inject constructor(
    private val session: AccountActivationSession,
    private val setPassword: SetInitialPasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CreateInitialPasswordUiState())
    val state = _state.asStateFlow()
    private val _completed = Channel<Unit>(Channel.BUFFERED)
    val completed = _completed.receiveAsFlow()
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, error = null) }
    fun onConfirmationChange(value: String) = _state.update { it.copy(confirmation = value, error = null) }
    fun togglePassword() = _state.update { it.copy(passwordVisible = !it.passwordVisible) }
    fun toggleConfirmation() = _state.update { it.copy(confirmationVisible = !it.confirmationVisible) }
    fun submit() {
        val current = _state.value
        if (current.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            setPassword(session.token, current.password, current.confirmation)
                .onSuccess { session.clear(); _completed.send(Unit) }
                .onFailure { failure -> _state.update { it.copy(error = failure.message ?: "No pudimos crear la contraseña.") } }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
