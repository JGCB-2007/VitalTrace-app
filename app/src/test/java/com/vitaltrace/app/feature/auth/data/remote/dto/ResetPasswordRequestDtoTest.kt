package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ResetPasswordRequestDtoTest {

    @Test
    fun `serializes recovery token using API code field`() {
        val request = ResetPasswordRequestDto(
            email = "patient@example.com",
            token = "110732",
            password = "SecurePassword123!",
            passwordConfirmation = "SecurePassword123!"
        )

        val payload = Json.parseToJsonElement(
            Json.encodeToString(request)
        ).jsonObject

        assertEquals("110732", payload.getValue("code").toString().trim('"'))
        assertFalse(payload.containsKey("token"))
    }
}
