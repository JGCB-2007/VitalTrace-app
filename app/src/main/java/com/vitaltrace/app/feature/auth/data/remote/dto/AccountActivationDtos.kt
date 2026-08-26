package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResendActivationCodeRequestDto(val email: String)

@Serializable
data class ActivateAccountRequestDto(
    val email: String,
    val code: String,
    val password: String,
    @SerialName("password_confirmation") val passwordConfirmation: String
)