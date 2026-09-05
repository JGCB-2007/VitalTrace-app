package com.vitaltrace.app.feature.auth.presentation

import com.vitaltrace.app.core.session.FakeAuthRepository
import com.vitaltrace.app.core.session.FakeTokenStore
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.UserRole
import com.vitaltrace.app.core.session.testUser
import com.vitaltrace.app.feature.auth.domain.AccountActivationSession
import com.vitaltrace.app.feature.auth.domain.usecase.LoginUseCase
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
class LoginViewModelTest {

    @Test
    fun `single patient role navigates straight to home`() = runTest {
        assertEquals(LoginUiEffect.NavigateToHome, firstEffect(setOf(UserRole.PATIENT)))
    }

    @Test
    fun `single nurse role navigates straight to the nurse portal`() = runTest {
        assertEquals(LoginUiEffect.NavigateToNursePortal, firstEffect(setOf(UserRole.NURSE)))
    }

    @Test
    fun `single relative role navigates straight to the relative portal`() = runTest {
        assertEquals(LoginUiEffect.NavigateToRelativePortal, firstEffect(setOf(UserRole.RELATIVE)))
    }

    @Test
    fun `patient plus nurse opens the portal selector`() = runTest {
        assertEquals(
            LoginUiEffect.NavigateToPortalSelector,
            firstEffect(setOf(UserRole.PATIENT, UserRole.NURSE))
        )
    }

    @Test
    fun `patient plus relative plus nurse opens the portal selector`() = runTest {
        assertEquals(
            LoginUiEffect.NavigateToPortalSelector,
            firstEffect(setOf(UserRole.PATIENT, UserRole.RELATIVE, UserRole.NURSE))
        )
    }

    @Test
    fun `non-mobile roles keep the existing home fallback`() = runTest {
        assertEquals(LoginUiEffect.NavigateToHome, firstEffect(setOf(UserRole.DOCTOR)))
    }

    private suspend fun TestScope.firstEffect(roles: Set<UserRole>): LoginUiEffect {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val session = SessionManager(FakeAuthRepository(testUser(roles)), FakeTokenStore(null))
            val viewModel = LoginViewModel(LoginUseCase(session), AccountActivationSession(), session)
            viewModel.onEmailChange("ana@example.com")
            viewModel.onPasswordChange("secret123")
            viewModel.login()
            advanceUntilIdle()
            return viewModel.uiEffect.first()
        } finally {
            Dispatchers.resetMain()
        }
    }
}
