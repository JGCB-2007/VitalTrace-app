package com.vitaltrace.app.core.session

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState
    data class Authenticated(val user: AuthenticatedUser) : SessionState
    data class Error(val message: String, val hasStoredToken: Boolean) : SessionState
}
