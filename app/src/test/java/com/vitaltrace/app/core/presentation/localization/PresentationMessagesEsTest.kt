package com.vitaltrace.app.core.presentation.localization

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Every centralized user-facing message must be non-blank Spanish, never English. */
class PresentationMessagesEsTest {

    private val englishGiveaways = listOf(
        "the ", "your ", "session", "password", "required", "invalid",
        "not received", "unexpected", "server", "request", "try again", "permission"
    )

    @Test fun `auth messages are populated and free of English`() {
        val messages = listOf(
            AuthMessagesEs.EMAIL_REQUIRED, AuthMessagesEs.EMAIL_INVALID,
            AuthMessagesEs.PASSWORD_REQUIRED, AuthMessagesEs.TOKEN_REQUIRED,
            AuthMessagesEs.PASSWORD_POLICY, AuthMessagesEs.PASSWORDS_MISMATCH,
            AuthMessagesEs.NETWORK, AuthMessagesEs.UNEXPECTED, AuthMessagesEs.LOGIN_FAILED,
            AuthMessagesEs.SESSION_INVALID, AuthMessagesEs.LOGOUT_FAILED,
            AuthMessagesEs.NO_ACTIVE_SESSION, AuthMessagesEs.AUTH_DATA_MISSING,
            AuthMessagesEs.TOKEN_MISSING, AuthMessagesEs.USER_DATA_MISSING,
            AuthMessagesEs.RESET_FAILED
        )
        messages.forEach { message ->
            assertTrue("blank auth message", message.isNotBlank())
            val lower = message.lowercase()
            englishGiveaways.forEach { token ->
                assertFalse("English leak in \"$message\"", lower.contains(token))
            }
        }
    }

    @Test fun `http status mapping is total and Spanish`() {
        val codes = listOf(400, 401, 403, 404, 408, 409, 422, 429, 500, 502, 503, 418, 599, 0)
        codes.forEach { code ->
            val message = HttpMessagesEs.forStatus(code)
            assertTrue("blank message for $code", message.isNotBlank())
            val lower = message.lowercase()
            englishGiveaways.forEach { token ->
                assertFalse("English leak for $code: \"$message\"", lower.contains(token))
            }
        }
    }
}
