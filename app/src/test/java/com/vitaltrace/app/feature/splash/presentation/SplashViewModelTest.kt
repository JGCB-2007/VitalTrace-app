package com.vitaltrace.app.feature.splash.presentation

import com.vitaltrace.app.core.session.FakeAuthRepository
import com.vitaltrace.app.core.session.FakeTokenStore
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.UserRole
import com.vitaltrace.app.core.session.testUser
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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @Test
    fun `single patient role navigates straight to home`() = runTest {
        assertEquals(SplashUiEffect.NavigateToHome, firstEffect(setOf(UserRole.PATIENT)))
    }

    @Test
    fun `single nurse role navigates straight to the nurse portal`() = runTest {
        assertEquals(SplashUiEffect.NavigateToNursePortal, firstEffect(setOf(UserRole.NURSE)))
    }

    @Test
    fun `single relative role navigates straight to the relative portal`() = runTest {
        assertEquals(SplashUiEffect.NavigateToRelativePortal, firstEffect(setOf(UserRole.RELATIVE)))
    }

    @Test
    fun `patient plus nurse opens the portal selector`() = runTest {
        assertEquals(
            SplashUiEffect.NavigateToPortalSelector,
            firstEffect(setOf(UserRole.PATIENT, UserRole.NURSE))
        )
    }

    @Test
    fun `patient plus relative plus nurse opens the portal selector`() = runTest {
        assertEquals(
            SplashUiEffect.NavigateToPortalSelector,
            firstEffect(setOf(UserRole.PATIENT, UserRole.RELATIVE, UserRole.NURSE))
        )
    }

    @Test
    fun `non-mobile roles fall back to home`() = runTest {
        assertEquals(SplashUiEffect.NavigateToHome, firstEffect(setOf(UserRole.DOCTOR)))
    }

    @Test
    fun `no stored session navigates to login`() = runTest {
        assertEquals(SplashUiEffect.NavigateToLogin, firstEffect(roles = null, token = null))
    }

    private suspend fun TestScope.firstEffect(
        roles: Set<UserRole>?,
        token: String? = "token"
    ): SplashUiEffect {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val session = SessionManager(
                FakeAuthRepository(roles?.let(::testUser)),
                FakeTokenStore(token)
            )
            val viewModel = SplashViewModel(session)
            advanceUntilIdle()
            return viewModel.effects.first()
        } finally {
            Dispatchers.resetMain()
        }
    }
}
