package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyActivationCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, code: String): Result<String> {
        val normalizedEmail = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalizedEmail)) return Result.failure(IllegalArgumentException("Ingresa un correo válido."))
        if (!CODE_REGEX.matches(code)) return Result.failure(IllegalArgumentException("El código debe contener 6 dígitos."))
        return repository.verifyActivationCode(normalizedEmail, code)
    }
}

class ResendActivationCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val normalizedEmail = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalizedEmail)) return Result.failure(IllegalArgumentException("Ingresa un correo válido."))
        return repository.resendActivationCode(normalizedEmail)
    }
}

class SetInitialPasswordUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(token: String, password: String, confirmation: String): Result<Unit> {
        if (token.isBlank()) return Result.failure(IllegalStateException("La verificación expiró. Solicita un código nuevo."))
        if (password.length < 8 || password.none(Char::isLetter) || password.none(Char::isDigit) || password.all(Char::isLetterOrDigit)) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, letras, números y un símbolo."))
        }
        if (password != confirmation) return Result.failure(IllegalArgumentException("Las contraseñas no coinciden."))
        return repository.setInitialPassword(token, password, confirmation)
    }
}

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
private val CODE_REGEX = Regex("^[0-9]{6}$")
