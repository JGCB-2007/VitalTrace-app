package com.vitaltrace.app.feature.auth.data.repository

import com.vitaltrace.app.core.datastore.TokenDataStore
import com.vitaltrace.app.core.network.AuthInterceptor
import com.vitaltrace.app.feature.auth.data.remote.AuthApiService
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginRequestDto
import com.vitaltrace.app.feature.auth.domain.repository.AuthException
import com.vitaltrace.app.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
                request = LoginRequestDto(
                    email = email,
                    password = password
                )
            )

            val loginData = response.data
                ?: return Result.failure(
                    AuthException(
                        response.message ?: "The server returned an invalid response."
                    )
                )

            tokenDataStore.saveToken(loginData.token)
            authInterceptor.updateToken(loginData.token)

            Result.success(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: HttpException) {
            Result.failure(
                AuthException(
                    when (exception.code()) {
                        401 -> "Incorrect email or password."
                        403 -> "Your account does not have permission to sign in."
                        422 -> "The submitted information is invalid."
                        429 -> "Too many attempts. Please try again later."
                        else -> "Authentication failed. Please try again."
                    }
                )
            )
        } catch (exception: IOException) {
            Result.failure(
                AuthException(
                    "Could not connect to the server."
                )
            )
        } catch (exception: Exception) {
            Result.failure(
                AuthException(
                    exception.message ?: "An unexpected error occurred."
                )
            )
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authApiService.logout()

            tokenDataStore.clearToken()
            authInterceptor.updateToken(null)

            Result.success(Unit)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: HttpException) {
            Result.failure(
                AuthException("Could not close the session.")
            )
        } catch (exception: IOException) {
            Result.failure(
                AuthException("Could not connect to the server.")
            )
        } catch (exception: Exception) {
            Result.failure(
                AuthException(
                    exception.message ?: "An unexpected error occurred."
                )
            )
        }
    }
}