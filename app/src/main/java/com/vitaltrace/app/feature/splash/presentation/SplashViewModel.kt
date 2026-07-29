package com.vitaltrace.app.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.feature.auth.domain.usecase.ValidateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val validateSessionUseCase: ValidateSessionUseCase
) : ViewModel() {

    private val effectChannel = Channel<SplashUiEffect>(
        capacity = Channel.BUFFERED
    )

    val effects = effectChannel.receiveAsFlow()

    init {
        validateSession()
    }

    private fun validateSession() {
        viewModelScope.launch {
            validateSessionUseCase()
                .onSuccess {
                    effectChannel.send(
                        SplashUiEffect.NavigateToHome
                    )
                }
                .onFailure {
                    effectChannel.send(
                        SplashUiEffect.NavigateToLogin
                    )
                }
        }
    }
}