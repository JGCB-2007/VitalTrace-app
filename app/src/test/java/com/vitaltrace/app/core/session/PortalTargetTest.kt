package com.vitaltrace.app.core.session

import org.junit.Assert.assertEquals
import org.junit.Test

class PortalTargetTest {

    @Test
    fun `patient role resolves to the patient portal only`() {
        assertEquals(
            listOf(PortalTarget.PATIENT),
            setOf(UserRole.PATIENT).availablePortals()
        )
    }

    @Test
    fun `relative role resolves to the relative portal only`() {
        assertEquals(
            listOf(PortalTarget.RELATIVE),
            setOf(UserRole.RELATIVE).availablePortals()
        )
    }

    @Test
    fun `nurse role resolves to the nurse portal only`() {
        assertEquals(
            listOf(PortalTarget.NURSE),
            setOf(UserRole.NURSE).availablePortals()
        )
    }

    @Test
    fun `patient plus nurse resolves to both portals in stable display order`() {
        assertEquals(
            listOf(PortalTarget.PATIENT, PortalTarget.NURSE),
            setOf(UserRole.NURSE, UserRole.PATIENT).availablePortals()
        )
    }

    @Test
    fun `patient plus relative plus nurse resolves in stable display order`() {
        assertEquals(
            listOf(PortalTarget.PATIENT, PortalTarget.RELATIVE, PortalTarget.NURSE),
            setOf(UserRole.NURSE, UserRole.RELATIVE, UserRole.PATIENT).availablePortals()
        )
    }

    @Test
    fun `non-mobile roles are ignored`() {
        assertEquals(
            emptyList<PortalTarget>(),
            setOf(
                UserRole.DOCTOR,
                UserRole.ADMISSION,
                UserRole.SYSTEM_ADMIN,
                UserRole.UNKNOWN
            ).availablePortals()
        )
    }

    @Test
    fun `non-mobile roles do not add portals next to a mobile role`() {
        assertEquals(
            listOf(PortalTarget.NURSE),
            setOf(UserRole.NURSE, UserRole.DOCTOR, UserRole.SYSTEM_ADMIN).availablePortals()
        )
    }

    @Test
    fun `post-auth rule routes zero one and many portals`() {
        fun classify(roles: Set<UserRole>) = roles.availablePortals().resolvePostAuthDestination(
            onNone = { "fallback" },
            onSingle = { "direct:$it" },
            onMultiple = { "selector" }
        )

        assertEquals("direct:PATIENT", classify(setOf(UserRole.PATIENT)))
        assertEquals("direct:NURSE", classify(setOf(UserRole.NURSE)))
        assertEquals("direct:RELATIVE", classify(setOf(UserRole.RELATIVE)))
        assertEquals("selector", classify(setOf(UserRole.PATIENT, UserRole.NURSE)))
        assertEquals(
            "selector",
            classify(setOf(UserRole.PATIENT, UserRole.RELATIVE, UserRole.NURSE))
        )
        assertEquals("fallback", classify(setOf(UserRole.DOCTOR)))
    }
}
