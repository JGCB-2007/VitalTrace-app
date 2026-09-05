package com.vitaltrace.app.core.presentation.localization

/**
 * Spanish validation / error strings for the auth flows.
 *
 * These replace the English literals that domain use cases and repositories
 * used to throw. Backend error *codes* are untouched; only the user-visible
 * message text is localized here.
 */
object AuthMessagesEs {
    const val EMAIL_REQUIRED = "Ingresa tu correo electrónico."
    const val EMAIL_INVALID = "Ingresa un correo electrónico válido."
    const val PASSWORD_REQUIRED = "Ingresa tu contraseña."
    const val TOKEN_REQUIRED = "Ingresa el token recibido por correo."
    const val PASSWORD_POLICY =
        "Usa al menos 8 caracteres, con letras, números y un símbolo."
    const val PASSWORDS_MISMATCH = "Las contraseñas no coinciden."

    const val NETWORK =
        "No pudimos conectar con VitalTrace. Revisa tu conexión e intenta de nuevo."
    const val UNEXPECTED = "Ocurrió un error inesperado. Intenta de nuevo."
    const val LOGIN_FAILED = "No pudimos completar el inicio de sesión. Intenta de nuevo."
    const val SESSION_INVALID = "No pudimos validar tu sesión. Inicia sesión nuevamente."
    const val LOGOUT_FAILED = "No pudimos cerrar la sesión en el servidor."
    const val NO_ACTIVE_SESSION = "No hay una sesión activa."
    const val AUTH_DATA_MISSING = "No recibimos los datos de autenticación. Intenta de nuevo."
    const val TOKEN_MISSING = "No recibimos el token de autenticación. Intenta de nuevo."
    const val USER_DATA_MISSING = "No recibimos los datos del usuario. Intenta de nuevo."
    const val RESET_FAILED =
        "No pudimos restablecer la contraseña. Verifica el token e inténtalo de nuevo."
}

/**
 * Centralized HTTP-status -> Spanish message mapping shared by the feature
 * repositories (patient, relative, nurse, and the generic branch of auth).
 *
 * A repository keeps only the status codes whose wording genuinely differs for
 * that feature and delegates everything else to [forStatus].
 */
object HttpMessagesEs {
    const val NETWORK =
        "No pudimos conectar con el servidor. Revisa tu conexión e intenta de nuevo."
    const val UNEXPECTED = "Ocurrió un error inesperado. Intenta de nuevo."

    fun forStatus(code: Int): String = when (code) {
        400 -> "No pudimos procesar la solicitud."
        401 -> "Tu sesión expiró. Inicia sesión nuevamente."
        403 -> "No tienes permiso para realizar esta acción."
        404 -> "El recurso solicitado no está disponible."
        408 -> "El servidor tardó demasiado en responder."
        409 -> "La solicitud entra en conflicto con el estado actual del servidor."
        422 -> "Revisa la información ingresada e intenta de nuevo."
        429 -> "Demasiados intentos. Espera un momento antes de volver a intentarlo."
        in 500..599 -> "El servidor no está disponible temporalmente. Intenta de nuevo más tarde."
        else -> "No pudimos completar la solicitud."
    }
}
