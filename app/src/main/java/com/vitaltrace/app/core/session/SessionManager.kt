package com.vitaltrace.app.core.session

import com.vitaltrace.app.core.presentation.localization.AuthMessagesEs
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) {
    private companion object {
        const val NO_SUPPORTED_PORTAL = "NO_SUPPORTED_PORTAL"
        const val NO_SUPPORTED_PORTAL_MESSAGE =
            "Este usuario no tiene acceso a ningún portal de la aplicación móvil."
    }

    private val operationMutex = Mutex()
    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    suspend fun restoreSession() = operationMutex.withLock {
        _state.value = SessionState.Loading
        val hasToken = !tokenStore.getToken().isNullOrBlank()

        if (!hasToken) {
            _state.value = SessionState.Unauthenticated
            return@withLock
        }

        authRepository.getCurrentUser()
            .onSuccess { user ->
                if (user.roles.availablePortals().isNotEmpty()) {
                    _state.value = SessionState.Authenticated(user)
                } else {
                    revokeUnsupportedSession()
                    _state.value = SessionState.Unauthenticated
                }
            }
            .onFailure { error ->
                _state.value = if ((error as? AuthException)?.httpCode == 401) {
                    tokenStore.clearToken()
                    SessionState.Unauthenticated
                } else {
                    SessionState.Error(
                        message = (error as? AuthException)?.message?.takeIf(String::isNotBlank)
                            ?: AuthMessagesEs.SESSION_INVALID,
                        hasStoredToken = true
                    )
                }
            }
    }

    suspend fun login(email: String, password: String): Result<Unit> = operationMutex.withLock {
        _state.value = SessionState.Loading
        val result = authRepository.login(email, password)
        val user = result.getOrElse { error ->
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(error)
        }

        if (user.roles.availablePortals().isEmpty()) {
            revokeUnsupportedSession()
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(
                AuthException(
                    message = NO_SUPPORTED_PORTAL_MESSAGE,
                    errorCode = NO_SUPPORTED_PORTAL
                )
            )
        }

        val verifiedUser = authRepository.getCurrentUser().getOrElse { error ->
            revokeUnsupportedSession()
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(error)
        }

        if (verifiedUser.roles.availablePortals().isEmpty()) {
            revokeUnsupportedSession()
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(
                AuthException(
                    message = NO_SUPPORTED_PORTAL_MESSAGE,
                    errorCode = NO_SUPPORTED_PORTAL
                )
            )
        }

        _state.value = SessionState.Authenticated(verifiedUser)
        Result.success(Unit)
    }

    suspend fun logout(): Result<Unit> = operationMutex.withLock {
        authRepository.logout()
        tokenStore.clearToken()
        _state.value = SessionState.Unauthenticated
        Result.success(Unit)
    }

    private suspend fun revokeUnsupportedSession() {
        authRepository.logout()
        tokenStore.clearToken()
    }
}