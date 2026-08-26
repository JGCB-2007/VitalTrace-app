package com.vitaltrace.app.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.AccountActivationSession
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val activationSession: AccountActivationSession
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<LoginUiEffect>(
        capacity = Channel.BUFFERED
    )
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun onPasswordVisibilityChange() {
        _uiState.update {
            it.copy(
                isPasswordVisible = !it.isPasswordVisible
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        if (currentState.isLoading) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            loginUseCase(
                email = currentState.email,
                password = currentState.password
            ).onSuccess {
                _uiState.update {
                    it.copy(isLoading = false)
                }

                _uiEffect.send(
                    LoginUiEffect.NavigateToHome
                )
            }.onFailure { exception ->
                val authException = exception as? AuthException
                if (authException?.errorCode == "ACCOUNT_ACTIVATION_REQUIRED") {
                    activationSession.begin(currentState.email)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                    _uiEffect.send(LoginUiEffect.NavigateToFirstAccess)
                    return@onFailure
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                            ?: "An unexpected error occurred."
                    )
                }
            }
        }
    }
}