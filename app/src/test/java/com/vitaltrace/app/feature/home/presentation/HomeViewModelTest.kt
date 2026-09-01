package com.vitaltrace.app.feature.home.presentation

import com.vitaltrace.app.core.session.AuthenticatedUser
import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.core.session.TokenStore
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import com.vitaltrace.app.feature.auth.domain.usecase.LogoutUseCase
import com.vitaltrace.app.feature.patient.domain.model.AlertsSummary
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.SummaryPatient
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientSummaryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Test
    fun `initial state is loading`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                FakePatientRepository(listOf(Result.success(summary())))
            )

            assertSame(HomeContentState.Loading, viewModel.uiState.value.contentState)
        }
    }

    @Test
    fun `successful summary load exposes real home content`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                FakePatientRepository(listOf(Result.success(summary())))
            )

            advanceUntilIdle()

            val success = assertType<HomeContentState.Success>(viewModel.uiState.value.contentState)
            assertEquals("Ana Martinez", success.content.patientName)
            assertEquals("AM", success.content.patientInitials)
            assertEquals("Confirmada", success.content.nextAppointment?.status)
            assertEquals("120", success.content.recentMeasurement?.value)
            assertEquals(listOf(0.65f), success.content.recentMeasurement?.chartValues)
            assertEquals(null, success.content.followUpStatus)
        }
    }

    @Test
    fun `summary failure exposes friendly error`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                FakePatientRepository(
                    listOf(Result.failure(IllegalStateException("technical details")))
                )
            )

            advanceUntilIdle()

            val error = assertType<HomeContentState.Error>(viewModel.uiState.value.contentState)
            assertEquals("No pudimos cargar tu información. Intenta de nuevo.", error.message)
        }
    }

    @Test
    fun `retry after failure loads summary once more`() = runTest {
        withMainDispatcher {
            val repository = FakePatientRepository(
                listOf(
                    Result.failure(IllegalStateException("offline")),
                    Result.success(summary())
                )
            )
            val viewModel = createViewModel(repository)
            advanceUntilIdle()
            assertType<HomeContentState.Error>(viewModel.uiState.value.contentState)

            viewModel.retry()

            assertSame(HomeContentState.Loading, viewModel.uiState.value.contentState)
            advanceUntilIdle()
            assertType<HomeContentState.Success>(viewModel.uiState.value.contentState)
            assertEquals(2, repository.summaryCalls)
        }
    }

    private suspend fun TestScope.withMainDispatcher(block: suspend () -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            block()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private inline fun <reified T> assertType(value: Any?): T {
        assertTrue("Expected ${T::class.simpleName}, but was ${value?.javaClass?.simpleName}", value is T)
        return value as T
    }

    private fun createViewModel(repository: PatientRepository): HomeViewModel {
        val sessionManager = SessionManager(FakeAuthRepository(), FakeTokenStore())
        return HomeViewModel(
            logoutUseCase = LogoutUseCase(sessionManager),
            getPatientSummary = GetPatientSummaryUseCase(repository),
            summaryMapper = HomeSummaryMapper()
        )
    }

    private fun summary() = PatientSummary(
        patient = SummaryPatient(1, "VT-0001", "ACTIVE", "Ana Martinez"),
        nextAppointment = Appointment(
            id = 25,
            scheduledAt = "2026-07-30 09:00:00",
            durationMinutes = 30,
            reason = "Control",
            status = "CONFIRMED",
            professional = null
        ),
        latestMeasurements = listOf(
            Measurement(
                id = 10,
                patientId = 1,
                measurementTypeId = 2,
                value = "120",
                unit = "mmHg",
                measuredAt = "2026-07-29 08:00:00",
                origin = "PATIENT",
                authorUserId = 3,
                observation = null,
                measurementType = null
            )
        ),
        activeTreatments = emptyList(),
        alerts = AlertsSummary(open = 0, critical = 0)
    )

    private class FakePatientRepository(
        results: List<Result<PatientSummary>>
    ) : PatientRepository {
        private val summaryResults = ArrayDeque(results)
        var summaryCalls = 0

        override suspend fun getSummary(): Result<PatientSummary> {
            summaryCalls += 1
            return summaryResults.removeFirst()
        }

        override suspend fun getAppointments(
            status: String?, dateFrom: String?, dateTo: String?, upcoming: Boolean?, page: Int?
        ): Result<Page<Appointment>> = unsupported()

        override suspend fun getMeasurements(
            measurementTypeId: Long?, dateFrom: String?, dateTo: String?, page: Int?
        ): Result<Page<Measurement>> = unsupported()

        override suspend fun createMeasurement(
            measurementTypeId: Long,
            value: Double,
            unit: String,
            measuredAt: String,
            observation: String?
        ): Result<Measurement> = unsupported()

        override suspend fun getTreatments(
            status: String?, dateFrom: String?, dateTo: String?, active: Boolean?, page: Int?
        ): Result<Page<Treatment>> = unsupported()

        private fun <T> unsupported(): Result<T> = Result.failure(UnsupportedOperationException())
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String): Result<AuthenticatedUser> =
            Result.failure(UnsupportedOperationException())

        override suspend fun getCurrentUser(): Result<AuthenticatedUser> =
            Result.failure(UnsupportedOperationException())

        override suspend fun logout(): Result<Unit> = Result.success(Unit)
    }

    private class FakeTokenStore : TokenStore {
        override suspend fun getToken(): String? = null
        override suspend fun saveToken(token: String) = Unit
        override suspend fun clearToken() = Unit
    }
}
