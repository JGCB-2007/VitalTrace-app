package com.vitaltrace.app.feature.auth.data.remote

import com.vitaltrace.app.core.network.ApiResponse
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginDataDto
import com.vitaltrace.app.feature.auth.data.remote.dto.LoginRequestDto
import com.vitaltrace.app.feature.auth.data.remote.dto.UserDto
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
}