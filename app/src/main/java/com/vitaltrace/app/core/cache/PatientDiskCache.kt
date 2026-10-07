package com.vitaltrace.app.core.cache

import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Measurement
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.Treatment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientDiskCache @Inject constructor(
    private val dao: PatientSnapshotDao,
    private val json: Json
) {
    suspend fun getAppointments(key: String): Page<Appointment>? = decode(key)
    suspend fun putAppointments(key: String, page: Page<Appointment>) = put(key, json.encodeToString(page))

    suspend fun getMeasurements(key: String): Page<Measurement>? = decode(key)
    suspend fun putMeasurements(key: String, page: Page<Measurement>) = put(key, json.encodeToString(page))

    suspend fun getTreatments(key: String): Page<Treatment>? = decode(key)
    suspend fun putTreatments(key: String, page: Page<Treatment>) = put(key, json.encodeToString(page))

    suspend fun invalidate(prefix: String) = dao.deleteByPrefix(prefix)
    suspend fun clear() = dao.clear()

    private suspend inline fun <reified T> decode(key: String): T? =
        dao.get(key)?.payload?.let { runCatching { json.decodeFromString<T>(it) }.getOrNull() }

    private suspend fun put(key: String, payload: String) {
        dao.put(PatientSnapshotEntity(key, payload))
    }
}
