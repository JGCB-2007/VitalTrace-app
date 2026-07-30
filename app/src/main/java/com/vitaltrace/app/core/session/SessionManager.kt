package com.vitaltrace.app.core.session

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
            .onSuccess { user -> _state.value = SessionState.Authenticated(user) }
            .onFailure { error ->
                _state.value = if ((error as? AuthException)?.httpCode == 401) {
                    tokenStore.clearToken()
                    SessionState.Unauthenticated
                } else {
                    SessionState.Error(
                        message = error.message ?: "Could not validate the current session.",
                        hasStoredToken = true
                    )
                }
            }
    }

    suspend fun login(email: String, password: String): Result<Unit> = operationMutex.withLock {
        _state.value = SessionState.Loading
        authRepository.login(email, password)
            .onSuccess { user -> _state.value = SessionState.Authenticated(user) }
            .onFailure { _state.value = SessionState.Unauthenticated }
            .map { Unit }
    }

    suspend fun logout(): Result<Unit> = operationMutex.withLock {
        authRepository.logout()
        tokenStore.clearToken()
        _state.value = SessionState.Unauthenticated
        Result.success(Unit)
    }
}
