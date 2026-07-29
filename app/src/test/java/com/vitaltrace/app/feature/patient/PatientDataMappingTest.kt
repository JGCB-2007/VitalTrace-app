package com.vitaltrace.app.feature.patient

import com.vitaltrace.app.core.network.ApiResponse
import com.vitaltrace.app.feature.patient.data.dto.appointments.AppointmentDto
import com.vitaltrace.app.feature.patient.data.dto.common.PaginatedResponseDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.CreateMeasurementRequestDto
import com.vitaltrace.app.feature.patient.data.dto.measurements.MeasurementDto
import com.vitaltrace.app.feature.patient.data.dto.summary.PatientSummaryDataDto
import com.vitaltrace.app.feature.patient.data.dto.treatments.TreatmentDto
import com.vitaltrace.app.feature.patient.data.mapper.toDomain
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PatientDataMappingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `summary parses and maps nullable appointment and empty lists`() {
        val response = json.decodeFromString<ApiResponse<PatientSummaryDataDto>>(
            """{"data":{"patient":{"id":1,"record_number":"VT-0001","administrative_status":"ACTIVE","full_name":"Ana Martinez"},"next_appointment":null,"latest_measurements":[],"active_treatments":[],"alerts_summary":{"open":2,"critical":1}},"message":null,"errors":null}"""
        )
        val summary = requireNotNull(response.data).toDomain()

        assertEquals("VT-0001", summary.patient.recordNumber)
        assertNull(summary.nextAppointment)
        assertTrue(summary.latestMeasurements.isEmpty())
        assertEquals(2, summary.alerts.open)
    }

    @Test
    fun `appointment preserves ATTENDED and nullable specialty`() {
        val dto = json.decodeFromString<AppointmentDto>(
            """{"id":25,"scheduled_at":"2026-07-30 09:00:00","duration_minutes":30,"reason":"Control","status":"ATTENDED","professional":{"id":8,"professional_type":"DOCTOR","full_name":"Carlos Ruiz","specialty":null}}"""
        )
        val appointment = dto.toDomain()

        assertEquals("ATTENDED", appointment.status)
        assertNull(appointment.professional?.specialty)
    }

    @Test
    fun `scalar measurement remains scalar`() {
        val dto = json.decodeFromString<MeasurementDto>(
            """{"id":4,"patient_id":1,"measurement_type_id":2,"value":"145.0000","unit":"mmHg","measured_at":"2026-07-29 09:42:00","origin":"PATIENT","author_user_id":7,"observation":null,"measurement_type":{"id":2,"name":"Systolic pressure","base_unit":"mmHg","decimals":0,"active":true}}"""
        )
        val measurement = dto.toDomain()

        assertEquals("145.0000", measurement.value)
        assertFalse(measurement.value.contains('/'))
        assertEquals("mmHg", measurement.measurementType?.baseUnit)
    }

    @Test
    fun `treatment mapper supports nullable diagnosis prescriber and end date`() {
        val dto = json.decodeFromString<TreatmentDto>(
            """{"id":9,"diagnosis_id":null,"diagnosis":null,"indications":"Continue plan","start_date":"2026-07-01","end_date":null,"status":"ACTIVE","prescribed_by":8,"prescriber":null}"""
        )
        val treatment = dto.toDomain()

        assertNull(treatment.diagnosis)
        assertNull(treatment.prescriber)
        assertNull(treatment.endDate)
    }

    @Test
    fun `empty Laravel page preserves links and nullable positions`() {
        val dto = json.decodeFromString<PaginatedResponseDto<AppointmentDto>>(
            """{"data":[],"links":{"first":"https://api.test/patient/appointments?page=1","last":"https://api.test/patient/appointments?page=1","prev":null,"next":null},"meta":{"current_page":1,"from":null,"last_page":1,"links":[],"path":"https://api.test/patient/appointments","per_page":15,"to":null,"total":0}}"""
        )
        val page = dto.toDomain { it.toDomain() }

        assertTrue(page.items.isEmpty())
        assertEquals(15, page.meta.perPage)
        assertNull(page.meta.from)
        assertNull(page.links.next)
    }

    @Test
    fun `create measurement request contains only patient supplied fields`() {
        val encoded = json.encodeToString(
            CreateMeasurementRequestDto(2, 145.0, "mmHg", "2026-07-29 09:42:00", null)
        )

        assertTrue(encoded.contains("measurement_type_id"))
        assertFalse(encoded.contains("patient_id"))
        assertFalse(encoded.contains("origin"))
        assertFalse(encoded.contains("author_user_id"))
    }
}
