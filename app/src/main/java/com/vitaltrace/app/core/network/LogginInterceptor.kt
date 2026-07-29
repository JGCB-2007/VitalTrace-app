package com.vitaltrace.app.core.network

import okhttp3.logging.HttpLoggingInterceptor

object LoggingInterceptor {

    fun create(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
