package com.vitaltrace.app.core.session

import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerTest {
    @Test
    fun `restore without token becomes unauthenticated without remote call`() = runBlocking {
        val fixture = fixture(token = null)

        fixture.manager.restoreSession()

        assertSame(SessionState.Unauthenticated, fixture.manager.state.value)
        assertEquals(0, fixture.repository.currentUserCalls)
    }

    @Test
    fun `valid token and auth me success becomes authenticated`() = runBlocking {
        val fixture = fixture(token = "valid-token")
        fixture.repository.currentUserResult = Result.success(user())

        fixture.manager.restoreSession()

        assertEquals(SessionState.Authenticated(user()), fixture.manager.state.value)
    }

    @Test
    fun `auth me 401 clears token and becomes unauthenticated`() = runBlocking {
        val fixture = fixture(token = "expired-token")
        fixture.repository.currentUserResult = Result.failure(
            AuthException("Expired", httpCode = 401)
        )

        fixture.manager.restoreSession()

        assertSame(SessionState.Unauthenticated, fixture.manager.state.value)
        assertNull(fixture.tokenStore.token)
    }

    @Test
    fun `successful login stores token and authenticates user`() = runBlocking {
        val fixture = fixture(token = null)
        fixture.repository.loginResult = Result.success(user())
        fixture.repository.tokenSavedByLogin = "new-token"

        val result = fixture.manager.login("ana@example.com", "secret")

        assertTrue(result.isSuccess)
        assertEquals("new-token", fixture.tokenStore.token)
        assertEquals(SessionState.Authenticated(user()), fixture.manager.state.value)
    }

    @Test
    fun `successful logout clears token and authenticated user`() = runBlocking {
        val fixture = authenticatedFixture()

        val result = fixture.manager.logout()

        assertTrue(result.isSuccess)
        assertNull(fixture.tokenStore.token)
        assertSame(SessionState.Unauthenticated, fixture.manager.state.value)
    }

    @Test
    fun `failed remote logout still clears local session`() = runBlocking {
        val fixture = authenticatedFixture()
        fixture.repository.logoutResult = Result.failure(AuthException("Server unavailable"))

        val result = fixture.manager.logout()

        assertTrue(result.isSuccess)
        assertNull(fixture.tokenStore.token)
        assertSame(SessionState.Unauthenticated, fixture.manager.state.value)
    }

    @Test
    fun `known role maps exactly`() {
        assertSame(UserRole.PATIENT, UserRole.fromApiValue("PATIENT"))
        assertSame(UserRole.RELATIVE, UserRole.fromApiValue("relative"))
        assertSame(UserRole.SYSTEM_ADMIN, UserRole.fromApiValue("system_admin"))
    }

    @Test
    fun `unknown role maps safely`() {
        assertSame(UserRole.UNKNOWN, UserRole.fromApiValue("NEW_FUTURE_ROLE"))
    }

    @Test
    fun `network failure during restore retains token and exposes error`() = runBlocking {
        val fixture = fixture(token = "possibly-valid-token")
        fixture.repository.currentUserResult = Result.failure(
            AuthException("Offline", isNetworkError = true)
        )

        fixture.manager.restoreSession()

        val state = fixture.manager.state.value as SessionState.Error
        assertTrue(state.hasStoredToken)
        assertEquals("possibly-valid-token", fixture.tokenStore.token)
    }

    @Test
    fun `single session state cannot retain authenticated user after failed login`() = runBlocking {
        val fixture = fixture(token = null)
        fixture.repository.loginResult = Result.failure(AuthException("Invalid credentials", httpCode = 422))

        val result = fixture.manager.login("ana@example.com", "bad-password")

        assertFalse(result.isSuccess)
        assertSame(SessionState.Unauthenticated, fixture.manager.state.value)
    }

    private suspend fun authenticatedFixture(): Fixture {
        val fixture = fixture(token = "valid-token")
        fixture.repository.currentUserResult = Result.success(user())
        fixture.manager.restoreSession()
        return fixture
    }

    private fun fixture(token: String?): Fixture {
        val tokenStore = FakeTokenStore(token)
        val repository = FakeAuthRepository(tokenStore)
        return Fixture(SessionManager(repository, tokenStore), repository, tokenStore)
    }

    private fun user() = AuthenticatedUser(
        id = 7,
        email = "ana@example.com",
        personId = 12,
        fullName = "Ana Martinez",
        roles = emptySet()
    )

    private data class Fixture(
        val manager: SessionManager,
        val repository: FakeAuthRepository,
        val tokenStore: FakeTokenStore
    )

    private class FakeTokenStore(initialToken: String?) : TokenStore {
        var token: String? = initialToken
        override suspend fun getToken(): String? = token
        override suspend fun saveToken(token: String) { this.token = token }
        override suspend fun clearToken() { token = null }
    }

    private class FakeAuthRepository(
        private val tokenStore: TokenStore
    ) : AuthRepository {
        var loginResult: Result<AuthenticatedUser> = Result.failure(AuthException("Not configured"))
        var currentUserResult: Result<AuthenticatedUser> = Result.failure(AuthException("Not configured"))
        var logoutResult: Result<Unit> = Result.success(Unit)
        var tokenSavedByLogin: String? = null
        var currentUserCalls = 0

        override suspend fun login(email: String, password: String): Result<AuthenticatedUser> {
            if (loginResult.isSuccess) tokenSavedByLogin?.let { tokenStore.saveToken(it) }
            return loginResult
        }

        override suspend fun getCurrentUser(): Result<AuthenticatedUser> {
            currentUserCalls += 1
            return currentUserResult
        }

        override suspend fun logout(): Result<Unit> = logoutResult
    }
}
