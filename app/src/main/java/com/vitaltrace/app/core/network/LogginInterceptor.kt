package com.vitaltrace.app.core.network

import okhttp3.logging.HttpLoggingInterceptor

object LoggingInterceptor {

    fun create(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}