package com.vitaltrace.app.core.session

import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository

/**
 * Minimal test doubles shared by the portal-selection tests. Only the auth surface
 * exercised by [SessionManager] is meaningful; every other call is unsupported.
 */
internal fun testUser(roles: Set<UserRole>) = AuthenticatedUser(
    id = 7,
    email = "ana@example.com",
    personId = 12,
    fullName = "Ana Martinez",
    roles = roles
)

internal class FakeTokenStore(private var token: String?) : TokenStore {
    override suspend fun getToken(): String? = token
    override suspend fun saveToken(token: String) { this.token = token }
    override suspend fun clearToken() { token = null }
    fun current(): String? = token
}

internal class FakeAuthRepository(
    private val user: AuthenticatedUser?
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthenticatedUser> =
        user?.let { Result.success(it) } ?: Result.failure(IllegalStateException("no user"))

    override suspend fun getCurrentUser(): Result<AuthenticatedUser> =
        user?.let { Result.success(it) } ?: Result.failure(IllegalStateException("no user"))

    override suspend fun logout(): Result<Unit> = Result.success(Unit)

    override suspend fun forgotPassword(email: String): Result<Unit> = unsupported()

    override suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> = unsupported()

    override suspend fun resendActivationCode(email: String): Result<Unit> = unsupported()

    override suspend fun activateAccount(
        email: String,
        code: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> = unsupported()

    private fun <T> unsupported(): Result<T> = Result.failure(UnsupportedOperationException())
}
