package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResendActivationCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val normalizedEmail = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            return Result.failure(IllegalArgumentException("Ingresa un correo válido."))
        }
        return repository.resendActivationCode(normalizedEmail)
    }
}

class ActivateAccountUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        code: String,
        password: String,
        confirmation: String
    ): Result<Unit> {
        val normalizedEmail = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalizedEmail)) {
            return Result.failure(IllegalArgumentException("Ingresa un correo válido."))
        }
        if (!CODE_REGEX.matches(code)) {
            return Result.failure(IllegalArgumentException("El código debe contener 6 dígitos."))
        }
        if (password.length < 8 ||
            password.none(Char::isLetter) ||
            password.none(Char::isDigit) ||
            password.all(Char::isLetterOrDigit)
        ) {
            return Result.failure(
                IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, letras, números y un símbolo."
                )
            )
        }
        if (password != confirmation) {
            return Result.failure(IllegalArgumentException("Las contraseñas no coinciden."))
        }
        return repository.activateAccount(normalizedEmail, code, password, confirmation)
    }
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
private val CODE_REGEX = Regex("^\\d{6}$")