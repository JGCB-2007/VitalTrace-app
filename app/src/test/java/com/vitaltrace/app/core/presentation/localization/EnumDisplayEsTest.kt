package com.vitaltrace.app.core.presentation.localization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the centralized Spanish enum/status mappings: known API codes always
 * resolve to Spanish, and no raw SCREAMING_SNAKE_CASE code ever leaks through.
 */
class EnumDisplayEsTest {

    // region Alert severity -------------------------------------------------

    @Test fun `alert severity maps the four canonical levels`() {
        assertEquals("Crítica", EnumDisplayEs.alertSeverity("CRITICAL"))
        assertEquals("Alta", EnumDisplayEs.alertSeverity("HIGH"))
        assertEquals("Media", EnumDisplayEs.alertSeverity("MEDIUM"))
        assertEquals("Baja", EnumDisplayEs.alertSeverity("LOW"))
    }

    @Test fun `alert severity is case and whitespace insensitive`() {
        assertEquals("Crítica", EnumDisplayEs.alertSeverity("  critical "))
        assertEquals("Media", EnumDisplayEs.alertSeverity("moderate"))
    }

    @Test fun `alert severity falls back to a neutral Spanish label`() {
        assertEquals("Desconocido", EnumDisplayEs.alertSeverity("SEV_9"))
        assertEquals("Desconocido", EnumDisplayEs.alertSeverity(null))
        assertEquals("Desconocido", EnumDisplayEs.alertSeverity(""))
    }

    // region Alert status -------------------------------------------------

    @Test fun `alert status maps the lifecycle codes from the audit`() {
        assertEquals("Nueva", EnumDisplayEs.alertStatus("NEW"))
        assertEquals("En revisión", EnumDisplayEs.alertStatus("IN_REVIEW"))
        assertEquals("Clasificada", EnumDisplayEs.alertStatus("CLASSIFIED"))
        assertEquals("Escalada", EnumDisplayEs.alertStatus("ESCALATED"))
    }

    @Test fun `alert status never returns the raw code`() {
        assertEquals("Desconocido", EnumDisplayEs.alertStatus("SOME_NEW_STATE"))
        assertNotEquals("SOME_NEW_STATE", EnumDisplayEs.alertStatus("SOME_NEW_STATE"))
    }

    // region Alert type / action ----------------------------------------

    @Test fun `alert type and action fall back instead of leaking a raw code`() {
        assertEquals("Desconocido", EnumDisplayEs.alertType("BRAND_NEW_RULE"))
        assertEquals("Desconocido", EnumDisplayEs.alertAction("BRAND_NEW_ACTION"))
        assertEquals("Clasificada", EnumDisplayEs.alertAction("CLASSIFY"))
        assertEquals("Escalada", EnumDisplayEs.alertAction("ESCALATE"))
    }

    // region Professional type ----------------------------------------

    @Test fun `professional type depends on display context`() {
        assertEquals(
            "Enfermero(a)",
            EnumDisplayEs.professionalType("NURSE", EnumDisplayEs.ProfessionalContext.PERSON)
        )
        assertEquals(
            "Enfermería",
            EnumDisplayEs.professionalType("NURSE", EnumDisplayEs.ProfessionalContext.DEPARTMENT)
        )
        assertEquals("Médico", EnumDisplayEs.professionalType("DOCTOR"))
    }

    @Test fun `professional type falls back for unknown roles`() {
        assertEquals("Desconocido", EnumDisplayEs.professionalType("ROBOT"))
        assertEquals("Desconocido", EnumDisplayEs.professionalType(null))
    }

    // region Appointment status ------------------------------------

    @Test fun `appointment status maps the audit examples`() {
        assertEquals("Programada", EnumDisplayEs.appointmentStatus("SCHEDULED"))
        assertEquals("Confirmada", EnumDisplayEs.appointmentStatus("CONFIRMED"))
        assertEquals("Cancelada", EnumDisplayEs.appointmentStatus("CANCELLED"))
        assertEquals("Cancelada", EnumDisplayEs.appointmentStatus("CANCELED"))
        assertEquals("Realizada", EnumDisplayEs.appointmentStatus("COMPLETED"))
        assertEquals("Realizada", EnumDisplayEs.appointmentStatus("ATTENDED"))
    }

    @Test fun `appointment status uses its own neutral fallback and never the raw code`() {
        assertEquals("Estado no disponible", EnumDisplayEs.appointmentStatus("RESCHEDULED"))
        assertEquals("Estado no disponible", EnumDisplayEs.appointmentStatus(null))
    }

    // region Clinical status --------------------------------------

    @Test fun `clinical status covers diagnosis, treatment and evolution codes`() {
        assertEquals("Activo", EnumDisplayEs.clinicalStatus("ACTIVE"))
        assertEquals("Finalizado", EnumDisplayEs.clinicalStatus("FINISHED"))
        assertEquals("Suspendido", EnumDisplayEs.clinicalStatus("SUSPENDED"))
        assertEquals("En revisión", EnumDisplayEs.clinicalStatus("UNDER_REVIEW"))
        assertEquals("Desconocido", EnumDisplayEs.clinicalStatus("WEIRD_STATE"))
    }

    // region No raw leak sweep -----------------------------------

    @Test fun `known codes never render as the raw uppercase code`() {
        val knownByMapper: Map<(String) -> String, List<String>> = mapOf(
            EnumDisplayEs::alertSeverity to listOf("CRITICAL", "HIGH", "MEDIUM", "LOW", "MODERATE"),
            EnumDisplayEs::alertStatus to listOf("NEW", "IN_REVIEW", "CLASSIFIED", "ESCALATED", "RESOLVED", "PENDING"),
            EnumDisplayEs::alertAction to listOf("CREATE", "CLASSIFY", "ESCALATE", "COMMENT", "REVIEW", "RESOLVE"),
            EnumDisplayEs::appointmentStatus to listOf("SCHEDULED", "CONFIRMED", "ATTENDED", "COMPLETED", "CANCELLED", "CANCELED", "NO_SHOW"),
            EnumDisplayEs::clinicalStatus to listOf("ACTIVE", "INACTIVE", "FINISHED", "SUSPENDED", "PENDING", "RESOLVED", "STABLE", "CRITICAL"),
            { code: String -> EnumDisplayEs.professionalType(code) } to listOf("NURSE", "DOCTOR", "SPECIALIST", "NUTRITIONIST"),
        )
        knownByMapper.forEach { (mapper, codes) ->
            codes.forEach { code ->
                val label = mapper(code)
                assertTrue("empty label for $code", label.isNotBlank())
                assertNotEquals("raw code leaked for $code", code, label)
                assertNotEquals("raw code leaked for $code", code.replace('_', ' '), label)
                assertTrue("label for $code is not Spanish text", label.first().isLetter())
            }
        }
    }
}
