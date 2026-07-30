package com.vitaltrace.app.feature.auth.domain.exception

class AuthException(
    message: String,
    cause: Throwable? = null,
    val httpCode: Int? = null,
    val isNetworkError: Boolean = false
) : Exception(message, cause)
