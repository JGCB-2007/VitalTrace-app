package com.vitaltrace.app.feature.portalselector.presentation

import com.vitaltrace.app.core.session.FakeAuthRepository
import com.vitaltrace.app.core.session.FakeTokenStore
import com.vitaltrace.app.core.session.PortalTarget
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.SessionState
import com.vitaltrace.app.core.session.UserRole
import com.vitaltrace.app.core.session.testUser
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortalSelectorViewModelTest {

    @Test
    fun `selector exposes only the portals available to the user`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(setOf(UserRole.PATIENT, UserRole.NURSE)).second
            assertEquals(
                listOf(PortalTarget.PATIENT, PortalTarget.NURSE),
                viewModel.state.value.portals
            )
        }
    }

    @Test
    fun `selector drops non-mobile roles`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                setOf(UserRole.RELATIVE, UserRole.DOCTOR, UserRole.SYSTEM_ADMIN)
            ).second
            assertEquals(listOf(PortalTarget.RELATIVE), viewModel.state.value.portals)
        }
    }

    @Test
    fun `logout returns to login and clears the session`() = runTest {
        withMainDispatcher {
            val (session, viewModel) = createViewModel(setOf(UserRole.PATIENT, UserRole.NURSE))

            viewModel.logout()
            advanceUntilIdle()

            assertEquals(PortalSelectorEffect.NavigateToLogin, viewModel.effects.first())
            assertSame(SessionState.Unauthenticated, session.state.value)
        }
    }

    private suspend fun createViewModel(
        roles: Set<UserRole>
    ): Pair<SessionManager, PortalSelectorViewModel> {
        val session = SessionManager(FakeAuthRepository(testUser(roles)), FakeTokenStore("token"))
        session.restoreSession()
        return session to PortalSelectorViewModel(session, LogoutUseCase(session))
    }

    private suspend fun TestScope.withMainDispatcher(block: suspend () -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }
}
