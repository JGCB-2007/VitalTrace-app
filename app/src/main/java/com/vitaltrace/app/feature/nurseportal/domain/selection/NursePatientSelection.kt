package com.vitaltrace.app.feature.nurseportal.domain.selection

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NursePatientSelection @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("nurse_patient_selection", Context.MODE_PRIVATE)
    private val selected = MutableStateFlow(prefs.getLong(KEY, -1L).takeIf { it != -1L })
    val selectedId = selected.asStateFlow()
    fun select(patientId: Long) { prefs.edit().putLong(KEY, patientId).apply(); selected.value = patientId }
    fun clear() { prefs.edit().remove(KEY).apply(); selected.value = null }
    private companion object { const val KEY = "patient_id" }
}