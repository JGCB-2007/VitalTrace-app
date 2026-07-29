package com.vitaltrace.app.feature.auth.data.repository

import com.vitaltrace.app.core.datastore.TokenDataStore
import com.vitaltrace.app.core.network.AuthInterceptor
import com.vitaltrace.app.feature.auth.data.remote.AuthApiService
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginRequestDto
import com.vitaltrace.app.feature.auth.domain.exception.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenDataStore: TokenDataStore,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            val response = authApiService.login(
                LoginRequestDto(
                    email = email,
                    password = password
                )
            )

            val token = response.data?.token
                ?: return Result.failure(
                    AuthException("Authentication token was not received.")
                )

            tokenDataStore.saveToken(token)
            authInterceptor.updateToken(token)

            Result.success(Unit)
        } catch (exception: HttpException) {
            Result.failure(
                AuthException(
                    message = getHttpErrorMessage(exception),
                    cause = exception
                )
            )
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "Could not connect to the server.",
                    cause = exception
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

    override suspend fun validateSession(): Result<Unit> {
        return try {
            val token = tokenDataStore.getToken()

            if (token.isNullOrBlank()) {
                return Result.failure(
                    AuthException("No active session was found.")
                )
            }

            authInterceptor.updateToken(token)

            authApiService.getAuthenticatedUser()

            Result.success(Unit)
        } catch (exception: HttpException) {
            if (exception.code() == 401) {
                clearLocalSession()
            }

            Result.failure(
                AuthException(
                    message = getHttpErrorMessage(exception),
                    cause = exception
                )
            )
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "Could not connect to the server.",
                    cause = exception
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
            clearLocalSession()

            Result.success(Unit)
        } catch (exception: HttpException) {
            clearLocalSession()

            Result.failure(
                AuthException(
                    message = getHttpErrorMessage(exception),
                    cause = exception
                )
            )
        } catch (exception: IOException) {
            clearLocalSession()

            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "Could not connect to the server.",
                    cause = exception
                )
            )
        } catch (exception: Exception) {
            clearLocalSession()

            Result.failure(
                AuthException(
                    message = exception.message
                        ?: "Could not close the session.",
                    cause = exception
                )
            )
        }
    }

    private suspend fun clearLocalSession() {
        tokenDataStore.clearToken()
        authInterceptor.updateToken(null)
    }

    private fun getHttpErrorMessage(
        exception: HttpException
    ): String {
        return when (exception.code()) {
            401 -> "Your session is invalid or has expired."
            403 -> "You do not have permission to perform this action."
            404 -> "The requested resource was not found."
            422 -> "The information provided is invalid."
            500 -> "The server encountered an internal error."
            else -> "Authentication request failed."
        }
    }
}