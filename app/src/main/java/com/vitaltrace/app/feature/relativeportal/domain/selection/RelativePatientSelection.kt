package com.vitaltrace.app.feature.relativeportal.domain.selection

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelativePatientSelection @Inject constructor(@ApplicationContext context: Context) {
    private val preferences = context.getSharedPreferences("relative_patient_selection", Context.MODE_PRIVATE)
    private val mutableSelectedId = MutableStateFlow(preferences.getLong(KEY_ID, NO_ID).takeIf { it != NO_ID })
    val selectedId: StateFlow<Long?> = mutableSelectedId.asStateFlow()

    fun select(patientId: Long) {
        preferences.edit().putLong(KEY_ID, patientId).apply()
        mutableSelectedId.value = patientId
    }

    fun clear() {
        preferences.edit().remove(KEY_ID).apply()
        mutableSelectedId.value = null
    }

    private companion object {
        const val KEY_ID = "patient_id"
        const val NO_ID = -1L
    }
}