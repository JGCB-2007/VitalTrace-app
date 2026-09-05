package com.vitaltrace.app.feature.nurseportal

import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAlert
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAppointment
import com.vitaltrace.app.feature.nurseportal.presentation.NursePortalUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Mirrors the [NursePortalUiState] mutations performed by NursePortalViewModel's
 * loadAppointments() (nurse-wide, Citas tab) and loadPatient() (selected-patient detail)
 * to guard against the two update paths sharing a single appointments field again.
 */
class NursePortalUiStateTest {
    private val nurseWideAppointment = appointment(id = 1, patientId = 10)
    private val patientAppointment = appointment(id = 2, patientId = 20)
    private val nurseWideAlert = alert(id = 100, patientId = 10, status = "NEW")
    private val patientAlert = alert(id = 200, patientId = 20, status = "IN_REVIEW")

    @Test fun `loading nurse-wide appointments populates only the nurse-wide field`() {
        val state = NursePortalUiState(patientAppointments = listOf(patientAppointment))

        val afterNurseLoad = state.copy(nurseAppointments = listOf(nurseWideAppointment), appointmentsError = null)

        assertEquals(listOf(nurseWideAppointment), afterNurseLoad.nurseAppointments)
        assertEquals(listOf(patientAppointment), afterNurseLoad.patientAppointments)
    }

    @Test fun `loading patient-detail appointments populates only the patient-specific field`() {
        val state = NursePortalUiState(nurseAppointments = listOf(nurseWideAppointment))

        val afterPatientLoad = state.copy(patientAppointments = listOf(patientAppointment))

        assertEquals(listOf(patientAppointment), afterPatientLoad.patientAppointments)
        assertEquals(listOf(nurseWideAppointment), afterPatientLoad.nurseAppointments)
    }

    @Test fun `selecting a patient does not overwrite the nurse-wide Citas list`() {
        val citasLoaded = NursePortalUiState(nurseAppointments = listOf(nurseWideAppointment))

        val afterSelectPatient = citasLoaded.copy(loading = true, selectedPatient = null)
        val afterPatientDetailLoaded = afterSelectPatient.copy(patientAppointments = listOf(patientAppointment), loading = false)

        assertEquals(listOf(nurseWideAppointment), afterPatientDetailLoaded.nurseAppointments)
    }

    @Test fun `loading nurse-wide alerts populates only the nurse-wide field`() {
        val state = NursePortalUiState(patientAlerts = listOf(patientAlert))

        val afterNurseLoad = state.copy(nurseAlerts = listOf(nurseWideAlert), alertsError = null)

        assertEquals(listOf(nurseWideAlert), afterNurseLoad.nurseAlerts)
        assertEquals(listOf(patientAlert), afterNurseLoad.patientAlerts)
    }

    @Test fun `loading patient alerts populates only the patient-specific field`() {
        val state = NursePortalUiState(nurseAlerts = listOf(nurseWideAlert))

        val afterPatientLoad = state.copy(patientAlerts = listOf(patientAlert), patientAlertsError = null)

        assertEquals(listOf(patientAlert), afterPatientLoad.patientAlerts)
        assertEquals(listOf(nurseWideAlert), afterPatientLoad.nurseAlerts)
    }

    @Test fun `clearing the selected patient does not wipe nurse-wide alerts`() {
        val state = NursePortalUiState(nurseAlerts = listOf(nurseWideAlert), patientAlerts = listOf(patientAlert))

        val afterClearPatient = state.copy(selectedPatient = null, patientAlerts = emptyList(), patientAlertsError = null)

        assertEquals(listOf(nurseWideAlert), afterClearPatient.nurseAlerts)
        assertEquals(emptyList<NurseAlert>(), afterClearPatient.patientAlerts)
    }

    @Test fun `refreshing nurse-wide alerts after classify does not wipe patient-specific alerts`() {
        val state = NursePortalUiState(nurseAlerts = listOf(nurseWideAlert), patientAlerts = listOf(patientAlert))

        val classifiedAlert = nurseWideAlert.copy(status = "CLASSIFIED")
        val afterClassifyRefresh = state.copy(nurseAlerts = listOf(classifiedAlert))

        assertEquals(listOf(classifiedAlert), afterClassifyRefresh.nurseAlerts)
        assertEquals(listOf(patientAlert), afterClassifyRefresh.patientAlerts)
    }

    @Test fun `refreshing patient alerts after escalate does not wipe nurse-wide alerts`() {
        val state = NursePortalUiState(nurseAlerts = listOf(nurseWideAlert), patientAlerts = listOf(patientAlert))

        val escalatedAlert = patientAlert.copy(status = "ESCALATED")
        val afterEscalateRefresh = state.copy(patientAlerts = listOf(escalatedAlert))

        assertEquals(listOf(nurseWideAlert), afterEscalateRefresh.nurseAlerts)
        assertEquals(listOf(escalatedAlert), afterEscalateRefresh.patientAlerts)
    }

    @Test fun `alerts with non-default statuses are preserved in state`() {
        val statuses = listOf("NEW", "IN_REVIEW", "CLASSIFIED", "ESCALATED")
        val alerts = statuses.mapIndexed { index, status -> alert(id = index.toLong(), patientId = 10, status = status) }

        val state = NursePortalUiState(nurseAlerts = alerts)

        assertEquals(statuses, state.nurseAlerts.map { it.status })
        assertEquals(4, state.nurseAlerts.size)
    }

    @Test fun `an alerts load failure surfaces an inline error without touching the other alerts list`() {
        val state = NursePortalUiState(nurseAlerts = listOf(nurseWideAlert), patientAlerts = listOf(patientAlert))

        val afterFailedNurseLoad = state.copy(alertsError = "No pudimos cargar las alertas.")

        assertEquals("No pudimos cargar las alertas.", afterFailedNurseLoad.alertsError)
        assertEquals(listOf(nurseWideAlert), afterFailedNurseLoad.nurseAlerts)
        assertNull(afterFailedNurseLoad.patientAlertsError)
        assertEquals(listOf(patientAlert), afterFailedNurseLoad.patientAlerts)
    }

    private fun appointment(id: Long, patientId: Long) = NurseAppointment(
        id = id,
        patientId = patientId,
        scheduledAt = "2026-09-05 09:30:00",
        durationMinutes = 30,
        reason = "Control",
        status = "SCHEDULED",
        professional = null
    )

    private fun alert(id: Long, patientId: Long, status: String) = NurseAlert(
        id = id,
        patientId = patientId,
        measurementId = null,
        type = "THRESHOLD",
        severity = "HIGH",
        status = status,
        description = "Alerta de prueba",
        generatedAt = "2026-09-03 09:00:00",
        closedAt = null,
        history = emptyList()
    )
}
