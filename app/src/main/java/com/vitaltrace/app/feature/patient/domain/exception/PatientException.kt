package com.vitaltrace.app.feature.patient.domain.exception

class PatientException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
