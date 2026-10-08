package com.vitaltrace.app.core.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appPreferencesDataStore by preferencesDataStore(name = "app_preferences")

enum class ThemePreference { SYSTEM, LIGHT, DARK }

data class AppPreferences(
    val onboardingCompleted: Boolean = false,
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val secureScreenEnabled: Boolean = false,
    val avatarUri: String? = null,
    val largeTextEnabled: Boolean = false
)

@Singleton
class AppPreferencesStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME = stringPreferencesKey("theme")
        val SECURE_SCREEN = booleanPreferencesKey("secure_screen_v2")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
    }

    val preferences: Flow<AppPreferences> = context.appPreferencesDataStore.data.map { values ->
        AppPreferences(
            onboardingCompleted = values[ONBOARDING_COMPLETED] ?: false,
            theme = runCatching {
                ThemePreference.valueOf(values[THEME] ?: ThemePreference.SYSTEM.name)
            }.getOrDefault(ThemePreference.SYSTEM),
            secureScreenEnabled = values[SECURE_SCREEN] ?: false,
            avatarUri = values[AVATAR_URI],
            largeTextEnabled = values[LARGE_TEXT] ?: false
        )
    }

    suspend fun completeOnboarding() = context.appPreferencesDataStore.edit {
        it[ONBOARDING_COMPLETED] = true
    }

    suspend fun setTheme(theme: ThemePreference) = context.appPreferencesDataStore.edit {
        it[THEME] = theme.name
    }

    suspend fun setSecureScreen(enabled: Boolean) = context.appPreferencesDataStore.edit {
        it[SECURE_SCREEN] = enabled
    }

    suspend fun setAvatarUri(uri: String?) = context.appPreferencesDataStore.edit {
        if (uri.isNullOrBlank()) it.remove(AVATAR_URI) else it[AVATAR_URI] = uri
    }

    suspend fun setLargeText(enabled: Boolean) = context.appPreferencesDataStore.edit {
        it[LARGE_TEXT] = enabled
    }
}
