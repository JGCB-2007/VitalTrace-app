package com.vitaltrace.app.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    @Volatile
    private var token: String? = null

    fun updateToken(newToken: String?) {
        token = newToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        if (token == null) {
            return chain.proceed(request)
        }

        val authenticatedRequest = request.newBuilder()
            .addHeader(
                "Authorization",
                "Bearer $token"
            )
            .build()

        return chain.proceed(authenticatedRequest)
    }
}