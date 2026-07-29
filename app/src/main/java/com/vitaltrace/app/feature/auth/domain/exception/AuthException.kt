package com.vitaltrace.app.feature.auth.domain.exception

class AuthException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)