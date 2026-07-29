package com.vitaltrace.app.core.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {

    @Volatile
    private var token: String? = null

    fun updateToken(token: String?) {
        this.token = token
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentToken = token

        val request = chain.request()
            .newBuilder()
            .apply {
                if (!currentToken.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $currentToken"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}