package com.vitaltrace.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaltrace.app.core.settings.AppPreferences
import com.vitaltrace.app.core.settings.AppPreferencesStore
import com.vitaltrace.app.core.settings.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppShellUiState(
    val loaded: Boolean = false,
    val preferences: AppPreferences = AppPreferences()
)

@HiltViewModel
class AppShellViewModel @Inject constructor(
    private val preferencesStore: AppPreferencesStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppShellUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesStore.preferences.collect { preferences ->
                _uiState.update {
                    it.copy(
                        loaded = true,
                        preferences = preferences
                    )
                }
            }
        }
    }

    fun completeOnboarding() = viewModelScope.launch { preferencesStore.completeOnboarding() }
    fun setTheme(value: ThemePreference) = viewModelScope.launch { preferencesStore.setTheme(value) }
    fun setSecureScreen(enabled: Boolean) = viewModelScope.launch {
        preferencesStore.setSecureScreen(enabled)
    }
}
