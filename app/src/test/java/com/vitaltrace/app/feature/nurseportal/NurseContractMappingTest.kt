package com.vitaltrace.app.feature.nurseportal

import com.vitaltrace.app.feature.nurseportal.data.dto.NurseMeasurementRequestDto
import com.vitaltrace.app.feature.nurseportal.data.dto.NursePatientDto
import com.vitaltrace.app.feature.nurseportal.data.mapper.toDomain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test fun `measurement request excludes server identity fields`() {
        val encoded = json.encodeToString(NurseMeasurementRequestDto(4, 120.0, "mmHg", "2026-09-03 09:00:00", "local test"))
        assertFalse(encoded.contains("patient_id"))
        assertFalse(encoded.contains("origin"))
        assertFalse(encoded.contains("author_user_id"))
        assertEquals(true, encoded.contains("measurement_type_id"))
    }
}