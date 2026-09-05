package com.vitaltrace.app.feature.nurseportal.data.repository

import com.vitaltrace.app.core.presentation.localization.HttpMessagesEs
import com.vitaltrace.app.feature.nurseportal.data.dto.*
import com.vitaltrace.app.feature.nurseportal.data.mapper.*
import com.vitaltrace.app.feature.nurseportal.data.remote.NursePortalApiService
import com.vitaltrace.app.feature.nurseportal.domain.repository.NurseRepository
import com.vitaltrace.app.feature.patient.domain.model.Page
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NursePortalException(val httpCode: Int?, message: String, cause: Throwable? = null) : Exception(message, cause)

class NurseRepositoryImpl @Inject constructor(private val api: NursePortalApiService) : NurseRepository {
    override suspend fun getDashboard() = execute { api.getSummary().data?.toDomain() ?: error("No se recibió el resumen de enfermería.") }
    override suspend fun getAssignedPatients(search: String?, page: Int?) = execute { api.getPatients(search, page).toNursePage { it.toDomain() } }
    override suspend fun getPatientProfile(patientId: Long) = execute { api.getPatientProfile(patientId).data?.toDomain() ?: error("No se recibió el perfil del paciente.") }
    override suspend fun getPatientSummary(patientId: Long) = execute { api.getPatientSummary(patientId).data?.toDomain() ?: error("No se recibió el resumen del paciente.") }
    override suspend fun getAppointments(page: Int?) = execute { api.getAppointments(page).toNursePage { it.toDomain() } }
    override suspend fun getPatientAppointments(patientId: Long, page: Int?) = execute { api.getPatientAppointments(patientId, page).toNursePage { it.toDomain() } }
    override suspend fun getAppointmentDetail(id: Long) = execute { api.getAppointmentDetail(id).data?.toDomain() ?: error("No se recibió el detalle de la cita.") }
    override suspend fun getMeasurements(patientId: Long, typeId: Long?, page: Int?) = execute { api.getPatientMeasurements(patientId, page, typeId).toNursePage { it.toDomain() } }
    override suspend fun createMeasurement(patientId: Long, typeId: Long, value: Double, unit: String, measuredAt: String, observation: String?) = execute { api.createMeasurement(patientId, NurseMeasurementRequestDto(typeId, value, unit, measuredAt, observation)).data?.toDomain() ?: error("No se recibió la medición creada.") }
    override suspend fun getMeasurementTypes() = execute { api.getMeasurementTypes().data.orEmpty().map { it.toDomain() } }
    override suspend fun getDiagnoses(patientId: Long, page: Int?) = execute { api.getPatientDiagnoses(patientId, page).toNursePage { it.toDomain() } }
    override suspend fun getTreatments(patientId: Long, page: Int?) = execute { api.getPatientTreatments(patientId, page).toNursePage { it.toDomain() } }
    override suspend fun getClinicalHistory(patientId: Long) = execute { api.getClinicalHistory(patientId).data?.toDomain() ?: error("No se recibió el historial clínico.") }
    override suspend fun getAlerts(status: String?, severity: String?, patientId: Long?, page: Int?) = execute { api.getAlerts(status, severity, patientId, page).toNursePage { it.toDomain() } }
    override suspend fun getPatientAlerts(patientId: Long, page: Int?) = execute { api.getPatientAlerts(patientId, page).toNursePage { it.toDomain() } }
    override suspend fun getAlertDetail(id: Long) = execute { api.getAlertDetail(id).data?.toDomain() ?: error("No se recibió el detalle de la alerta.") }
    override suspend fun classifyAlert(id: Long, comment: String?) = execute { api.classifyAlert(id, NurseAlertActionDto(comment)).data?.toDomain() ?: error("No se pudo clasificar la alerta.") }
    override suspend fun escalateAlert(id: Long, comment: String?) = execute { api.escalateAlert(id, NurseAlertActionDto(comment)).data?.toDomain() ?: error("No se pudo escalar la alerta.") }

    private suspend fun <T> execute(block: suspend () -> T): Result<T> = try { Result.success(block()) } catch (e: HttpException) { Result.failure(NursePortalException(e.code(), messageFor(e.code()), e)) } catch (e: IOException) { Result.failure(NursePortalException(null, HttpMessagesEs.NETWORK, e)) } catch (e: IllegalStateException) { Result.failure(NursePortalException(null, e.message ?: HttpMessagesEs.UNEXPECTED, e)) } catch (e: Exception) { Result.failure(NursePortalException(null, HttpMessagesEs.UNEXPECTED, e)) }
    // Feature-specific wording for 403/422; the rest uses the shared mapping.
    private fun messageFor(code: Int) = when (code) { 403 -> "No tienes autorización para consultar este recurso."; 422 -> "Revisa los datos ingresados."; else -> HttpMessagesEs.forStatus(code) }
}