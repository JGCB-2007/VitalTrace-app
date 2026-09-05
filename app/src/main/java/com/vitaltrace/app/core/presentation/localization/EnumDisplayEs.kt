package com.vitaltrace.app.core.presentation.localization

/**
 * Centralized Spanish display strings for enum-like / status-like API codes.
 *
 * Rules:
 * - Raw API codes stay internal (API calls, colors, branching). These helpers
 *   are for user-visible rendering only.
 * - Known codes always resolve to a Spanish label.
 * - Unknown / blank codes never render raw: callers get a neutral Spanish
 *   fallback instead of the SCREAMING_SNAKE_CASE value.
 */
object EnumDisplayEs {

    /** Neutral fallback for an unrecognized enum-like code. */
    const val UNKNOWN: String = "Desconocido"

    /** Neutral fallback for an unrecognized appointment status. */
    const val UNKNOWN_APPOINTMENT_STATUS: String = "Estado no disponible"

    private fun key(value: String?): String = value?.trim()?.uppercase().orEmpty()

    // region Alerts ----------------------------------------------------------

    /** Alert severity: CRITICAL / HIGH / MEDIUM / LOW (and common synonyms). */
    fun alertSeverity(value: String?): String = when (key(value)) {
        "CRITICAL", "CRITICO", "CRÍTICA", "CRÍTICO" -> "Crítica"
        "HIGH", "ALTA", "ALTO" -> "Alta"
        "MEDIUM", "MODERATE", "MODERADA", "MODERADO", "MEDIA" -> "Media"
        "LOW", "BAJA", "BAJO" -> "Baja"
        else -> UNKNOWN
    }

    /** Alert lifecycle status: NEW / IN_REVIEW / CLASSIFIED / ESCALATED / ... */
    fun alertStatus(value: String?): String = when (key(value)) {
        "NEW", "NUEVA" -> "Nueva"
        "IN_REVIEW", "UNDER_REVIEW", "REVIEWING", "IN-REVIEW" -> "En revisión"
        "CLASSIFIED", "CLASIFICADA" -> "Clasificada"
        "ESCALATED", "ESCALADA" -> "Escalada"
        "RESOLVED", "CLOSED", "RESUELTA" -> "Resuelta"
        "PENDING", "PENDIENTE" -> "Pendiente"
        else -> UNKNOWN
    }

    /** Rule / category that produced the alert (alert "type"). */
    fun alertType(value: String?): String = when (key(value)) {
        "THRESHOLD", "THRESHOLD_EXCEEDED", "OUT_OF_RANGE" -> "Umbral excedido"
        "HIGH_GLUCOSE", "GLUCOSE_HIGH" -> "Glucosa alta"
        "LOW_GLUCOSE", "GLUCOSE_LOW" -> "Glucosa baja"
        "HIGH_BLOOD_PRESSURE", "BLOOD_PRESSURE_HIGH", "HYPERTENSION" -> "Presión arterial alta"
        "LOW_BLOOD_PRESSURE", "BLOOD_PRESSURE_LOW", "HYPOTENSION" -> "Presión arterial baja"
        "HIGH_HEART_RATE", "HEART_RATE_HIGH", "TACHYCARDIA" -> "Frecuencia cardíaca alta"
        "LOW_HEART_RATE", "HEART_RATE_LOW", "BRADYCARDIA" -> "Frecuencia cardíaca baja"
        "MISSED_MEASUREMENT", "NO_MEASUREMENT", "MISSING_MEASUREMENT" -> "Medición no registrada"
        "TREND", "TREND_ANOMALY", "ANOMALY" -> "Tendencia anómala"
        "MANUAL", "MANUAL_REVIEW" -> "Revisión manual"
        else -> UNKNOWN
    }

    /** Action recorded in the alert history timeline. */
    fun alertAction(value: String?): String = when (key(value)) {
        "CREATE", "CREATED", "GENERATED", "OPEN", "OPENED" -> "Creada"
        "CLASSIFY", "CLASSIFIED", "CLASSIFICATION" -> "Clasificada"
        "ESCALATE", "ESCALATED", "ESCALATION" -> "Escalada"
        "COMMENT", "COMMENTED", "NOTE", "NOTE_ADDED" -> "Comentario"
        "REVIEW", "REVIEWED", "IN_REVIEW", "START_REVIEW" -> "En revisión"
        "RESOLVE", "RESOLVED", "CLOSE", "CLOSED" -> "Resuelta"
        "REOPEN", "REOPENED" -> "Reabierta"
        "ASSIGN", "ASSIGNED" -> "Asignada"
        else -> UNKNOWN
    }

    // endregion

    // region Professionals -------------------------------------------------

    /**
     * How a professional-type code should read: as a department / area name,
     * or as a person's title.
     */
    enum class ProfessionalContext { DEPARTMENT, PERSON }

    /**
     * Professional type code -> Spanish. `NURSE` reads "Enfermería" as an area
     * and "Enfermero(a)" as a person's title; [context] selects which.
     */
    fun professionalType(
        value: String?,
        context: ProfessionalContext = ProfessionalContext.PERSON
    ): String {
        val person = context == ProfessionalContext.PERSON
        return when (key(value)) {
            "NURSE", "ENFERMERIA", "ENFERMERÍA", "NURSING" ->
                if (person) "Enfermero(a)" else "Enfermería"
            "DOCTOR", "PHYSICIAN", "MEDIC", "MEDICO", "MÉDICO" ->
                if (person) "Médico" else "Medicina"
            "SPECIALIST", "ESPECIALISTA" ->
                if (person) "Especialista" else "Especialidades"
            "NUTRITIONIST", "NUTRICIONISTA" -> "Nutricionista"
            "PSYCHOLOGIST", "PSICOLOGO", "PSICÓLOGO" -> if (person) "Psicólogo(a)" else "Psicología"
            "ADMIN", "ADMINISTRATOR", "ADMINISTRADOR" ->
                if (person) "Administrador(a)" else "Administración"
            "ADMISSION", "ADMISSIONS", "ADMISION", "ADMISIÓN" -> "Admisión"
            "CAREGIVER", "CUIDADOR" -> "Cuidador(a)"
            "RELATIVE", "FAMILY", "FAMILIAR" -> "Familiar"
            "PATIENT", "PACIENTE" -> "Paciente"
            else -> UNKNOWN
        }
    }

    // endregion

    // region Appointments ------------------------------------------------

    /**
     * Appointment status code -> Spanish. Mirrors the strings used by the
     * Compose `AppointmentStatusChip` so every surface reads the same.
     */
    fun appointmentStatus(value: String?): String = when (key(value)) {
        "SCHEDULED", "PROGRAMADA" -> "Programada"
        "CONFIRMED", "CONFIRMADA" -> "Confirmada"
        "ATTENDED", "COMPLETED", "DONE", "REALIZADA", "COMPLETADA" -> "Realizada"
        "CANCELLED", "CANCELED", "CANCELADA" -> "Cancelada"
        "NO_SHOW", "NOSHOW", "NO-SHOW", "ABSENT" -> "No asistió"
        else -> UNKNOWN_APPOINTMENT_STATUS
    }

    // endregion

    // region Clinical statuses -----------------------------------------

    /**
     * Clinical status codes shared by diagnoses, treatments and clinical
     * evolutions (used by list chips and the PDF export).
     */
    fun clinicalStatus(value: String?): String = when (key(value)) {
        "ACTIVE", "ACTIVO", "ACTIVA" -> "Activo"
        "INACTIVE", "INACTIVO" -> "Inactivo"
        "FINISHED", "COMPLETED", "COMPLETE", "FINALIZADO" -> "Finalizado"
        "SUSPENDED", "PAUSED", "SUSPENDIDO" -> "Suspendido"
        "CANCELLED", "CANCELED", "CANCELADO" -> "Cancelado"
        "PENDING", "PENDIENTE" -> "Pendiente"
        "RESOLVED", "RESUELTO", "RESUELTA" -> "Resuelto"
        "UNDER_REVIEW", "IN_REVIEW", "REVIEWING" -> "En revisión"
        "STABLE", "ESTABLE" -> "Estable"
        "OBSERVATION", "UNDER_OBSERVATION", "EN_OBSERVACION" -> "En observación"
        "DELICATE", "DELICADO" -> "Delicado"
        "CRITICAL", "CRITICO", "CRÍTICO" -> "Crítico"
        "RECOVERY", "IN_RECOVERY", "RECUPERACION" -> "En recuperación"
        "CONFIRMED" -> "Confirmada"
        "PRESUMPTIVE", "PROVISIONAL" -> "Presuntivo"
        "RULED_OUT", "DISCARDED" -> "Descartado"
        else -> UNKNOWN
    }

    // endregion
}
