package com.vitaltrace.app.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import com.vitaltrace.app.core.session.TokenStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(
    name = "vitaltrace_preferences"
)

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenStore {

    private companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[AUTH_TOKEN]
    }

    override suspend fun getToken(): String? {
        return token.first()
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
        }
    }
}
