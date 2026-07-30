package com.vitaltrace.app.feature.auth.data.remote

import com.vitaltrace.app.core.network.ApiResponse
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginDataDto
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.UserDto
import com.vitaltrace.app.feature.auth.data.remote.dto.ForgotPasswordRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.ResetPasswordRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): ApiResponse<LoginDataDto>

    @GET("auth/me")
    suspend fun getAuthenticatedUser(): ApiResponse<UserDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequestDto
    ): ApiResponse<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequestDto
    ): ApiResponse<Unit>
}
