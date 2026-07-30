package com.vitaltrace.app.feature.appointments.presentation

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PaginationLinks
import com.vitaltrace.app.feature.patient.domain.model.PaginationMeta
import com.vitaltrace.app.feature.patient.domain.model.PatientSummary
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import com.vitaltrace.app.feature.patient.domain.usecase.GetPatientAppointmentsUseCase
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
class AppointmentsViewModelTest {
    @Test
    fun `initial state is loading`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                FakePatientRepository(listOf(Result.success(appointmentsPage())))
            )

            assertSame(AppointmentsContentState.Loading, viewModel.uiState.value.contentState)
        }
    }

    @Test
    fun `successful load exposes appointments from repository`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                FakePatientRepository(listOf(Result.success(appointmentsPage())))
            )

            advanceUntilIdle()

            val success = assertType<AppointmentsContentState.Success>(
                viewModel.uiState.value.contentState
            )
            assertEquals(2L, success.content.nextAppointment?.id)
            assertEquals(listOf(1L), success.content.upcomingAppointments.map { it.id })
            assertEquals(AppointmentStatus.ATTENDED, success.content.previousAppointments.single().status)
        }
    }

    @Test
    fun `repository failure exposes friendly error`() = runTest {
        withMainDispatcher {
            val viewModel = createViewModel(
                FakePatientRepository(
                    listOf(Result.failure(IllegalStateException("server details")))
                )
            )

            advanceUntilIdle()

            val error = assertType<AppointmentsContentState.Error>(
                viewModel.uiState.value.contentState
            )
            assertEquals("No pudimos cargar tus citas. Intenta de nuevo.", error.message)
        }
    }

    @Test
    fun `retry after error requests appointments again`() = runTest {
        withMainDispatcher {
            val repository = FakePatientRepository(
                listOf(
                    Result.failure(IllegalStateException("offline")),
                    Result.success(appointmentsPage())
                )
            )
            val viewModel = createViewModel(repository)
            advanceUntilIdle()
            assertType<AppointmentsContentState.Error>(viewModel.uiState.value.contentState)

            viewModel.retry()

            assertSame(AppointmentsContentState.Loading, viewModel.uiState.value.contentState)
            advanceUntilIdle()
            assertType<AppointmentsContentState.Success>(viewModel.uiState.value.contentState)
            assertEquals(2, repository.appointmentCalls)
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

    private fun createViewModel(repository: PatientRepository): AppointmentsViewModel {
        return AppointmentsViewModel(
            getPatientAppointments = GetPatientAppointmentsUseCase(repository),
            appointmentsMapper = AppointmentsMapper()
        )
    }

    private fun appointmentsPage() = Page(
        items = listOf(
            appointment(1, "2026-08-05 09:00:00", "CONFIRMED"),
            appointment(2, "2026-07-30 09:00:00", "SCHEDULED"),
            appointment(3, "2026-06-25 10:30:00", "ATTENDED")
        ),
        links = PaginationLinks("first", "last", null, null),
        meta = PaginationMeta(1, 1, 1, emptyList(), "path", 15, 3, 3)
    )

    private fun appointment(id: Long, scheduledAt: String, status: String) = Appointment(
        id = id,
        scheduledAt = scheduledAt,
        durationMinutes = 30,
        reason = "Control",
        status = status,
        professional = null
    )

    private inline fun <reified T> assertType(value: Any?): T {
        assertTrue("Expected ${T::class.simpleName}, but was ${value?.javaClass?.simpleName}", value is T)
        return value as T
    }

    private class FakePatientRepository(
        results: List<Result<Page<Appointment>>>
    ) : PatientRepository {
        private val appointmentResults = ArrayDeque(results)
        var appointmentCalls = 0

        override suspend fun getAppointments(
            status: String?, dateFrom: String?, dateTo: String?, upcoming: Boolean?, page: Int?
        ): Result<Page<Appointment>> {
            appointmentCalls += 1
            return appointmentResults.removeFirst()
        }

        override suspend fun getSummary(): Result<PatientSummary> = unsupported()

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
}
