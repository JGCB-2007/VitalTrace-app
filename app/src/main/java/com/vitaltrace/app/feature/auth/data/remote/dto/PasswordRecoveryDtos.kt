package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequestDto(val email: String)

@Serializable
data class ResetPasswordRequestDto(
    val email: String,
    val token: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)
