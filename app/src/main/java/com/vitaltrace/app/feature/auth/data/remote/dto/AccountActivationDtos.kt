package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class VerifyActivationCodeRequestDto(val email: String, val code: String)
@Serializable data class ActivationTokenDto(@SerialName("activation_token") val activationToken: String, @SerialName("expires_in") val expiresIn: Int)
@Serializable data class ResendActivationCodeRequestDto(val email: String)
@Serializable data class SetInitialPasswordRequestDto(@SerialName("activation_token") val activationToken: String, val password: String, @SerialName("password_confirmation") val passwordConfirmation: String)
@Serializable data class ActivationCompletedDto(@SerialName("activation_completed") val activationCompleted: Boolean)
