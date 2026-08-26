package com.vitaltrace.app.feature.auth.presentation.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.AccountActivationSession
import com.vitaltrace.app.feature.auth.domain.usecase.ActivateAccountUseCase
import com.vitaltrace.app.feature.auth.domain.usecase.ResendActivationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FirstAccessEmailUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ActivationCodeUiState(
    val email: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val resendMessage: String? = null
)

data class CreateInitialPasswordUiState(
    val password: String = "",
    val confirmation: String = "",
    val passwordVisible: Boolean = false,
    val confirmationVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FirstAccessEmailViewModel @Inject constructor(
    private val session: AccountActivationSession,
    private val resendCode: ResendActivationCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FirstAccessEmailUiState(email = session.email))
    val state = _state.asStateFlow()
    private val _continue = Channel<Unit>(Channel.BUFFERED)
    val continueFlow = _continue.receiveAsFlow()

    fun onEmailChange(value: String) =
        _state.update { it.copy(email = value, error = null) }

    fun continueToCode() {
        if (_state.value.isLoading) return
        val email = _state.value.email.trim().lowercase()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            resendCode(email)
                .onSuccess {
                    session.begin(email)
                    _continue.send(Unit)
                }
                .onFailure { failure ->
                    _state.update {
                        it.copy(error = failure.message ?: "No pudimos solicitar el código.")
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

@HiltViewModel
class ActivationCodeViewModel @Inject constructor(
    private val session: AccountActivationSession,
    private val resendCode: ResendActivationCodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ActivationCodeUiState(email = session.email))
    val state = _state.asStateFlow()
    private val _verified = Channel<Unit>(Channel.BUFFERED)
    val verified = _verified.receiveAsFlow()

    fun onCodeChange(value: String) =
        _state.update {
            it.copy(
                code = value.filter(Char::isDigit).take(6),
                error = null,
                resendMessage = null
            )
        }

    fun continueToPassword() {
        val code = _state.value.code
        if (!Regex("^\\d{6}$").matches(code)) {
            _state.update { it.copy(error = "El código debe contener 6 dígitos.") }
            return
        }
        session.codeEntered(code)
        viewModelScope.launch { _verified.send(Unit) }
    }

    fun resend() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, resendMessage = null) }
            resendCode(session.email)
                .onSuccess {
                    _state.update {
                        it.copy(resendMessage = "Se envió un nuevo código.")
                    }
                }
                .onFailure { failure ->
                    _state.update {
                        it.copy(error = failure.message ?: "No pudimos reenviar el código.")
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}

@HiltViewModel
class CreateInitialPasswordViewModel @Inject constructor(
    private val session: AccountActivationSession,
    private val activateAccount: ActivateAccountUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CreateInitialPasswordUiState())
    val state = _state.asStateFlow()
    private val _completed = Channel<Unit>(Channel.BUFFERED)
    val completed = _completed.receiveAsFlow()

    fun onPasswordChange(value: String) =
        _state.update { it.copy(password = value, error = null) }

    fun onConfirmationChange(value: String) =
        _state.update { it.copy(confirmation = value, error = null) }

    fun togglePassword() =
        _state.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun toggleConfirmation() =
        _state.update { it.copy(confirmationVisible = !it.confirmationVisible) }

    fun submit() {
        val current = _state.value
        if (current.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            activateAccount(
                email = session.email,
                code = session.code,
                password = current.password,
                confirmation = current.confirmation
            )
                .onSuccess {
                    session.clear()
                    _completed.send(Unit)
                }
                .onFailure { failure ->
                    _state.update {
                        it.copy(error = failure.message ?: "No pudimos activar la cuenta.")
                    }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }
}