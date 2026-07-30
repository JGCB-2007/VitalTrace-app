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
import com.vitaltrace.app.feature.auth.data.remote.dto.ResendActivationCodeRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.SetInitialPasswordRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.VerifyActivationCodeRequestDto
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
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
            val responseBody = exception.response()?.errorBody()?.string()
            val activationRequired = responseBody?.contains("ACCOUNT_ACTIVATION_REQUIRED") == true
            Result.failure(
                AuthException(
                    message = if (activationRequired) {
                        "Debes completar tu primer acceso antes de iniciar sesión."
                    } else {
                        getHttpErrorMessage(exception)
                    },
                    cause = exception,
                    httpCode = exception.code(),
                    errorCode = if (activationRequired) "ACCOUNT_ACTIVATION_REQUIRED" else null
                )
            )
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "Could not connect to the server.",
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
                    message = exception.message
                        ?: "Could not connect to the server.",
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
                    message = exception.message ?: "Could not connect to the server.",
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
                message = exception.message ?: "Could not connect to the server.",
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

    override suspend fun verifyActivationCode(email: String, code: String): Result<String> {
        return runCatching {
            authApiService.verifyActivationCode(
                VerifyActivationCodeRequestDto(email, code)
            ).data?.activationToken ?: throw IllegalStateException()
        }
    }

    override suspend fun resendActivationCode(email: String): Result<Unit> = executePublicRequest {
        authApiService.resendActivationCode(ResendActivationCodeRequestDto(email))
    }

    override suspend fun setInitialPassword(
        activationToken: String,
        password: String,
        passwordConfirmation: String
    ): Result<Unit> = executePublicRequest {
        authApiService.setInitialPassword(
            SetInitialPasswordRequestDto(activationToken, password, passwordConfirmation)
        )
    }

    private suspend fun clearLocalSession() {
        tokenStore.clearToken()
        authInterceptor.updateToken(null)
        patientMemoryCache.clear()
    }

    private fun getHttpErrorMessage(
        exception: HttpException
    ): String {
        return when (exception.code()) {
            400 -> "The request could not be processed."
            401 -> "Your email or password is incorrect, or your session has expired."
            403 -> "You do not have permission to perform this action."
            404 -> "The requested resource was not found."
            408 -> "The server took too long to respond."
            409 -> "The request conflicts with the current server state."
            422 -> "The information provided is invalid."
            429 -> "Too many requests. Please try again later."
            500 -> "The server encountered an internal error."
            502 -> "The server is temporarily unavailable."
            503 -> "The service is temporarily unavailable."
            else -> "The authentication request failed."
        }
    }
}
