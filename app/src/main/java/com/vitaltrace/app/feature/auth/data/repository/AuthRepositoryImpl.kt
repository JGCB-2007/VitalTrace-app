package com.vitaltrace.app.feature.auth.data.repository

import com.vitaltrace.app.core.network.AuthInterceptor
import com.vitaltrace.app.core.cache.PatientMemoryCache
import com.vitaltrace.app.core.session.AuthenticatedUser
import com.vitaltrace.app.core.session.TokenStore
import com.vitaltrace.app.feature.auth.data.mapper.toAuthenticatedUser
import com.vitaltrace.app.feature.auth.data.remote.AuthApiService
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.ForgotPasswordRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.ResetPasswordRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.ActivateAccountRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.ResendActivationCodeRequestDto
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStore: TokenStore,
    private val authInterceptor: AuthInterceptor,
    private val patientMemoryCache: PatientMemoryCache
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthenticatedUser> {
        return try {
            val response = authApiService.login(
                LoginRequestDto(
                    email = email,
                    password = password
                )
            )

            val data = response.data ?: return Result.failure(
                AuthException("Authentication data was not received.")
            )
            val token = data.token

            if (token.isNullOrBlank()) {
                return Result.failure(
                    AuthException(
                        message = "Authentication token was not received."
                    )
                )
            }

            tokenStore.saveToken(token)
            authInterceptor.updateToken(token)

            Result.success(data.user.toAuthenticatedUser())
        } catch (exception: HttpException) {
            Result.failure(toAuthException(exception))
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    message = "No pudimos conectar con VitalTrace. Revisa tu conexión e intenta de nuevo.",
                    cause = exception,
                    isNetworkError = true
                )
            )
        } catch (exception: Exception) {
            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "An unexpected authentication error occurred.",
                    cause = exception
                )
            )
        }
    }

    override suspend fun getCurrentUser(): Result<AuthenticatedUser> {
        return try {
            val token = tokenStore.getToken()

            if (token.isNullOrBlank()) {
                return Result.failure(
                    AuthException(
                        message = "No active session was found."
                    )
                )
            }

            authInterceptor.updateToken(token)

            val user = authApiService.getAuthenticatedUser().data
                ?: return Result.failure(AuthException("Authenticated user data was not received."))

            Result.success(user.toAuthenticatedUser())
        } catch (exception: HttpException) {
            if (exception.code() == 401) {
                clearLocalSession()
            }

            Result.failure(
                AuthException(
                    message = getHttpErrorMessage(exception),
                    cause = exception,
                    httpCode = exception.code()
                )
            )
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    message = "No pudimos conectar con VitalTrace. Revisa tu conexión e intenta de nuevo.",
                    cause = exception,
                    isNetworkError = true
                )
            )
        } catch (exception: Exception) {
            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "Could not validate the current session.",
                    cause = exception
                )
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authApiService.logout()
            Result.success(Unit)
        } catch (exception: HttpException) {
            if (exception.code() != 401) {
                return Result.failure(
                    AuthException(
                        message = getHttpErrorMessage(exception),
                        cause = exception,
                        httpCode = exception.code()
                    )
                )
            }

            Result.success(Unit)
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    message = "No pudimos conectar con VitalTrace. Revisa tu conexión e intenta de nuevo.",
                    cause = exception,
                    isNetworkError = true
                )
            )
        } catch (exception: Exception) {
            Result.failure(
                AuthException(
                    message = exception.message ?: "Could not close the remote session.",
                    cause = exception
                )
            )
        } finally {
            clearLocalSession()
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return executePublicRequest {
            authApiService.forgotPassword(ForgotPasswordRequestDto(email))
        }
    }

    override suspend fun resetPassword(
        email: String,
        token: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> {
        return executePublicRequest {
            authApiService.resetPassword(
                ResetPasswordRequestDto(email, token, password, passwordConfirmation)
            )
        }
    }

    private suspend fun executePublicRequest(
        request: suspend () -> Unit
    ): Result<Unit> = try {
        request()
        Result.success(Unit)
    } catch (exception: HttpException) {
        Result.failure(
            AuthException(
                message = getHttpErrorMessage(exception),
                cause = exception,
                httpCode = exception.code()
            )
        )
    } catch (exception: IOException) {
        Result.failure(
            AuthException(
                message = "No pudimos conectar con VitalTrace. Revisa tu conexión e intenta de nuevo.",
                cause = exception,
                isNetworkError = true
            )
        )
    } catch (exception: Exception) {
        Result.failure(
            AuthException(
                message = exception.message ?: "The authentication request failed.",
                cause = exception
            )
        )
    }

    override suspend fun resendActivationCode(email: String): Result<Unit> =
        executeActivationRequest {
            authApiService.resendActivationCode(ResendActivationCodeRequestDto(email))
        }

    override suspend fun activateAccount(
        email: String,
        code: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> = executeActivationRequest {
        authApiService.activateAccount(
            ActivateAccountRequestDto(
                email = email,
                code = code,
                password = password,
                passwordConfirmation = passwordConfirmation
            )
        )
    }

    private suspend fun executeActivationRequest(
        request: suspend () -> Unit
    ): Result<Unit> = try {
        request()
        Result.success(Unit)
    } catch (exception: HttpException) {
        Result.failure(toActivationException(exception))
    } catch (exception: IOException) {
        Result.failure(
            AuthException(
                message = "No pudimos conectar con VitalTrace. Revisa tu conexión e intenta de nuevo.",
                cause = exception,
                isNetworkError = true
            )
        )
    } catch (exception: Exception) {
        Result.failure(
            AuthException(
                message = "No pudimos completar la activación. Intenta de nuevo.",
                cause = exception
            )
        )
    }
    private suspend fun clearLocalSession() {
        tokenStore.clearToken()
        authInterceptor.updateToken(null)
        patientMemoryCache.clear()
    }

    private fun getHttpErrorMessage(exception: HttpException): String =
        toAuthException(exception).message.orEmpty()

    private fun toAuthException(exception: HttpException): AuthException {
        val body = exception.response()?.errorBody()?.string()
        val backend = parseBackendError(body)
        val details = listOfNotNull(backend.message, backend.fieldMessage)
            .joinToString(" ")
            .lowercase()

        val message = when {
            backend.code == "ACCOUNT_ACTIVATION_REQUIRED" ->
                "Debes completar tu primer acceso antes de iniciar sesión."
            exception.code() == 401 ->
                "Tu sesión no es válida o ha expirado. Inicia sesión nuevamente."
            exception.code() == 403 ->
                "Esta cuenta no tiene permiso para acceder a la aplicación móvil de pacientes."
            exception.code() == 422 && "credentials are incorrect" in details ->
                "El correo electrónico o la contraseña son incorrectos."
            exception.code() == 422 && "not active" in details ->
                "Esta cuenta está inactiva o bloqueada. Comunícate con soporte."
            exception.code() == 422 ->
                "Revisa los datos ingresados e intenta de nuevo."
            exception.code() == 429 ->
                "Demasiados intentos. Espera un momento antes de volver a intentarlo."
            exception.code() in 500..599 ->
                "VitalTrace no está disponible temporalmente. Intenta de nuevo más tarde."
            else ->
                "No pudimos completar el inicio de sesión. Intenta de nuevo."
        }

        return AuthException(
            message = message,
            cause = exception,
            httpCode = exception.code(),
            errorCode = backend.code
        )
    }

    private fun parseBackendError(body: String?): BackendError {
        if (body.isNullOrBlank()) return BackendError()
        return runCatching {
            val root = Json.parseToJsonElement(body).jsonObject
            val message = root["message"]?.jsonPrimitive?.contentOrNull
            val errors = runCatching { root["errors"]?.jsonObject }.getOrNull()
            val code = errors?.get("code")?.jsonPrimitive?.contentOrNull
            val fieldMessage = errors
                ?.entries
                ?.firstOrNull { it.key != "code" }
                ?.value
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonPrimitive
                ?.contentOrNull
            BackendError(message, code, fieldMessage)
        }.getOrDefault(BackendError())
    }

    private fun toActivationException(exception: HttpException): AuthException {
        val backend = parseBackendError(exception.response()?.errorBody()?.string())
        val details = listOfNotNull(backend.message, backend.fieldMessage)
            .joinToString(" ")
            .lowercase()

        val message = when {
            exception.code() == 429 ->
                "Demasiados intentos. Espera un momento antes de volver a intentarlo."
            exception.code() in 500..599 ->
                "VitalTrace no está disponible temporalmente. Intenta de nuevo más tarde."
            exception.code() == 422 &&
                ("invalid or has expired" in details || "activation code" in details) ->
                "El código es incorrecto, expiró o ya fue utilizado."
            exception.code() == 422 && "confirmation" in details ->
                "Las contraseñas no coinciden."
            exception.code() == 422 && "password" in details ->
                "La contraseña no cumple los requisitos de seguridad."
            exception.code() == 422 && "email" in details ->
                "El correo no es válido o no puede activarse."
            exception.code() == 422 ->
                "Revisa el correo, el código y la contraseña e intenta de nuevo."
            else ->
                "No pudimos completar la activación. Intenta de nuevo."
        }

        return AuthException(
            message = message,
            cause = exception,
            httpCode = exception.code(),
            errorCode = backend.code
        )
    }
    private data class BackendError(
        val message: String? = null,
        val code: String? = null,
        val fieldMessage: String? = null
    )
}
