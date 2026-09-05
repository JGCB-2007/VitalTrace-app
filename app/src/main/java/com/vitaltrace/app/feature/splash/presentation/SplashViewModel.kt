package com.vitaltrace.app.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.core.session.PortalTarget
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.SessionState
import com.vitaltrace.app.core.session.availablePortals
import com.vitaltrace.app.core.session.resolvePostAuthDestination
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
            val state = sessionManager.state.value
            val effect = if (state is SessionState.Authenticated) {
                state.user.roles.availablePortals().resolvePostAuthDestination(
                    onNone = { SplashUiEffect.NavigateToHome },
                    onSingle = { portal ->
                        when (portal) {
                            PortalTarget.PATIENT -> SplashUiEffect.NavigateToHome
                            PortalTarget.RELATIVE -> SplashUiEffect.NavigateToRelativePortal
                            PortalTarget.NURSE -> SplashUiEffect.NavigateToNursePortal
                        }
                    },
                    onMultiple = { SplashUiEffect.NavigateToPortalSelector }
                )
            } else {
                SplashUiEffect.NavigateToLogin
            }
            effectChannel.send(effect)
        }
    }
}
