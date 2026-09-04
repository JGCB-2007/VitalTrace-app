package com.vitaltrace.app.feature.portalselector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.core.session.PortalTarget
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.SessionState
import com.vitaltrace.app.core.session.availablePortals
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortalSelectorUiState(
    val portals: List<PortalTarget> = emptyList()
)

sealed interface PortalSelectorEffect {
    data object NavigateToLogin : PortalSelectorEffect
}

@HiltViewModel
class PortalSelectorViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(
        PortalSelectorUiState(
            portals = (sessionManager.state.value as? SessionState.Authenticated)
                ?.user?.roles?.availablePortals().orEmpty()
        )
    )
    val state = _state.asStateFlow()

    private val effectChannel = Channel<PortalSelectorEffect>(Channel.BUFFERED)
    val effects = effectChannel.receiveAsFlow()

    fun logout() = viewModelScope.launch {
        logoutUseCase()
        effectChannel.send(PortalSelectorEffect.NavigateToLogin)
    }
}
