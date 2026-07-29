package com.vitaltrace.app.feature.auth.domain.repository

class AuthException(
    override val message: String
) : Exception(message)