package com.vitaltrace.app.feature.nurseportal

import com.vitaltrace.app.feature.nurseportal.data.dto.NurseAlertDto
import com.vitaltrace.app.feature.nurseportal.data.dto.NurseAppointmentDto
import com.vitaltrace.app.feature.nurseportal.data.dto.NurseMeasurementRequestDto
import com.vitaltrace.app.feature.nurseportal.data.dto.NursePatientDto
import com.vitaltrace.app.feature.nurseportal.data.mapper.toDomain
import com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NurseContractMappingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test fun `patient mapper preserves last measurement and next appointment ownership`() {
        val dto = json.decodeFromString<NursePatientDto>("""{"patient_id":2,"record_number":"VT-002","full_name":"Ana Ruiz","administrative_status":"ACTIVE","last_measurement":{"id":9,"patient_id":2,"value":"120","unit":"mmHg","measured_at":"2026-09-03 09:00:00","origin":"NURSE","measurement_type":{"id":4,"name":"Pressure","base_unit":"mmHg","decimals":0}},"next_appointment":{"id":7,"patient_id":2,"scheduled_at":"2026-09-05 09:30:00","duration_minutes":30,"reason":"Control","status":"SCHEDULED"}}""")
        val patient = dto.toDomain()
        assertEquals(2L, patient.id)
        assertEquals("120", patient.lastMeasurement?.value)
        assertEquals(7L, patient.nextAppointment?.id)
        assertEquals(2L, patient.nextAppointment?.patientId)
    }

    @Test fun `appointment professional with nested specialty object deserializes correctly`() {
        val dto = json.decodeFromString<NurseAppointmentDto>(
            """{"id":7,"patient_id":2,"scheduled_at":"2026-09-05 09:30:00","duration_minutes":30,"reason":"Control","status":"SCHEDULED","professional":{"id":3,"full_name":"Dra. Padilla","professional_type":"NURSE","specialty":{"id":5,"name":"Enfermería"}}}"""
        )
        assertEquals("Enfermería", dto.professional?.specialty?.name)
        assertEquals(5L, dto.professional?.specialty?.id)
    }

    @Test fun `appointment mapper extracts specialty name into domain professional`() {
        val dto = json.decodeFromString<NurseAppointmentDto>(
            """{"id":7,"patient_id":2,"scheduled_at":"2026-09-05 09:30:00","duration_minutes":30,"reason":"Control","status":"SCHEDULED","professional":{"id":3,"full_name":"Dra. Padilla","professional_type":"NURSE","specialty":{"id":5,"name":"Enfermería"}}}"""
        )
        val appointment = dto.toDomain()
        assertEquals("Enfermería", appointment.professional?.specialty)
    }

    @Test fun `appointment mapper tolerates missing specialty`() {
        val dto = json.decodeFromString<NurseAppointmentDto>(
            """{"id":7,"patient_id":2,"scheduled_at":"2026-09-05 09:30:00","duration_minutes":30,"reason":"Control","status":"SCHEDULED","professional":{"id":3,"full_name":"Dra. Padilla","professional_type":"NURSE"}}"""
        )
        assertNull(dto.toDomain().professional?.specialty)
    }

    @Test fun `alert response deserializes all fields including history`() {
        val dto = json.decodeFromString<NurseAlertDto>(
            """{"id":11,"patient_id":2,"measurement_id":9,"type":"THRESHOLD","severity":"CRITICAL","status":"NEW","description":"Presión fuera de rango","generated_at":"2026-09-03 09:05:00","closed_at":null,"history":[{"id":1,"action":"CREATED","previous_status":null,"new_status":"NEW","comment":null,"created_at":"2026-09-03 09:05:00"}]}"""
        )
        assertEquals(11L, dto.id)
        assertEquals(2L, dto.patientId)
        assertEquals(9L, dto.measurementId)
        assertEquals("CRITICAL", dto.severity)
        assertEquals("NEW", dto.status)
        assertNull(dto.closedAt)
        assertEquals(1, dto.history.size)
        assertEquals("CREATED", dto.history.first().action)

        val alert = dto.toDomain()
        assertEquals(11L, alert.id)
        assertEquals("CRITICAL", alert.severity)
        assertEquals(1, alert.history.size)
    }

    @Test fun `alert response tolerates missing optional fields`() {
        val dto = json.decodeFromString<NurseAlertDto>(
            """{"id":12,"patient_id":3,"type":"THRESHOLD","severity":"MODERATE","status":"IN_REVIEW","description":"Frecuencia elevada","generated_at":"2026-09-03 10:00:00"}"""
        )
        assertNull(dto.measurementId)
        assertNull(dto.closedAt)
        assertTrue(dto.history.isEmpty())
    }

    @Test fun `paginated alerts response deserializes every status without client-side filtering`() {
        val page = json.decodeFromString<PaginatedResponseDto<NurseAlertDto>>(
            """{"data":[
                {"id":1,"patient_id":2,"type":"THRESHOLD","severity":"CRITICAL","status":"NEW","description":"a","generated_at":"2026-09-03 09:00:00"},
                {"id":2,"patient_id":2,"type":"THRESHOLD","severity":"HIGH","status":"IN_REVIEW","description":"b","generated_at":"2026-09-03 09:01:00"},
                {"id":3,"patient_id":3,"type":"THRESHOLD","severity":"MODERATE","status":"CLASSIFIED","description":"c","generated_at":"2026-09-03 09:02:00"},
                {"id":4,"patient_id":3,"type":"THRESHOLD","severity":"LOW","status":"ESCALATED","description":"d","generated_at":"2026-09-03 09:03:00"}
            ],"links":{"first":"p1","last":"p1"},"meta":{"current_page":1,"last_page":1,"path":"p","per_page":20,"total":4}}"""
        )
        val statuses = page.data.map { it.status }
        assertEquals(listOf("NEW", "IN_REVIEW", "CLASSIFIED", "ESCALATED"), statuses)
        assertEquals(4, page.data.size)
    }

    @Test fun `measurement request excludes server identity fields`() {
        val encoded = json.encodeToString(NurseMeasurementRequestDto(4, 120.0, "mmHg", "2026-09-03 09:00:00", "local test"))
        assertFalse(encoded.contains("patient_id"))
        assertFalse(encoded.contains("origin"))
        assertFalse(encoded.contains("author_user_id"))
        assertEquals(true, encoded.contains("measurement_type_id"))
    }
}