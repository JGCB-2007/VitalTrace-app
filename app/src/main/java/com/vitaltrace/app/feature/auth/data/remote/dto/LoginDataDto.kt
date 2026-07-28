package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDataDto(
    val user: UserDto,
    val token: String,

    @SerialName("token_type")
    val tokenType: String
)