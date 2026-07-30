package com.vitaltrace.app.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager
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
            sessionManager.restoreSession()
            val effect = when (sessionManager.state.value) {
                is SessionState.Authenticated -> SplashUiEffect.NavigateToHome
                else -> SplashUiEffect.NavigateToLogin
            }
            effectChannel.send(effect)
        }
    }
}
