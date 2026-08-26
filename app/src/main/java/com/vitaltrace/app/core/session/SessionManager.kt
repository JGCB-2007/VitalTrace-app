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
    private companion object {
        const val PATIENT_ACCESS_REQUIRED = "PATIENT_ACCESS_REQUIRED"
        const val PATIENT_ACCESS_MESSAGE =
            "Este usuario no tiene acceso a la aplicación móvil de pacientes."
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
                if (UserRole.PATIENT in user.roles) {
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
                        message = error.message ?: "Could not validate the current session.",
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

        if (UserRole.PATIENT !in user.roles) {
            revokeUnsupportedSession()
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(
                AuthException(
                    message = PATIENT_ACCESS_MESSAGE,
                    errorCode = PATIENT_ACCESS_REQUIRED
                )
            )
        }

        val verifiedUser = authRepository.getCurrentUser().getOrElse { error ->
            revokeUnsupportedSession()
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(error)
        }

        if (UserRole.PATIENT !in verifiedUser.roles) {
            revokeUnsupportedSession()
            _state.value = SessionState.Unauthenticated
            return@withLock Result.failure(
                AuthException(
                    message = PATIENT_ACCESS_MESSAGE,
                    errorCode = PATIENT_ACCESS_REQUIRED
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