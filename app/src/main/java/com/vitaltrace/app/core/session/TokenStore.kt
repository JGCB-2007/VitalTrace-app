package com.vitaltrace.app.core.session

interface TokenStore {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
