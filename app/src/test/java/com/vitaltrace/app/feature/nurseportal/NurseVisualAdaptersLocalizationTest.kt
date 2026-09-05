package com.vitaltrace.app.feature.nurseportal

import com.vitaltrace.app.feature.nurseportal.domain.model.NurseAppointment
import com.vitaltrace.app.feature.nurseportal.domain.model.NurseProfessional
import com.vitaltrace.app.feature.nurseportal.presentation.toAppointmentDetailUiModel
import com.vitaltrace.app.feature.nurseportal.presentation.toAppointmentUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The nurse appointment adapters must render the API `yyyy-MM-dd HH:mm:ss`
 * timestamp as localized es-NI date/time and the professional type code as a
 * Spanish label, never the raw wire values.
 */
class NurseVisualAdaptersLocalizationTest {

    private fun appointment(
        scheduledAt: String = "2026-09-05 09:30:00",
        type: String? = "NURSE"
    ) = NurseAppointment(
        id = 1,
        patientId = 2,
        scheduledAt = scheduledAt,
        durationMinutes = 30,
        reason = "Control",
        status = "SCHEDULED",
        professional = NurseProfessional(3, "Dra. Padilla", type, "Cardiología")
    )

    @Test fun `appointment UI model localizes the API timestamp`() {
        val model = appointment().toAppointmentUiModel("Ana Martínez")

        assertTrue(model.date, model.date.contains("2026"))
        assertFalse("raw ISO date leaked", model.date.contains("2026-09-05"))
        assertTrue(model.time, model.time.contains("9:30"))
        assertFalse("raw wire time leaked", model.time.contains("09:30:00"))
    }

    @Test fun `appointment detail UI model localizes timestamp and professional type`() {
        val model = appointment().toAppointmentDetailUiModel(patientName = "Ana Martínez")

        assertFalse("raw ISO date leaked", model.date.contains("2026-09-05"))
        assertFalse("raw wire time leaked", model.time.contains("09:30:00"))
        assertTrue(model.specialty, model.specialty.contains("Enfermero(a)"))
        assertFalse("raw enum code leaked", model.specialty.contains("NURSE"))
        assertTrue(model.specialty, model.specialty.contains("Cardiología"))
    }

    @Test fun `adapter degrades gracefully for an unparseable timestamp`() {
        val model = appointment(scheduledAt = "2026-09-05").toAppointmentUiModel("Ana Martínez")

        assertEquals("2026-09-05", model.date)
        assertEquals("", model.time)
    }
}
