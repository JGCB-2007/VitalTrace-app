package com.vitaltrace.app.feature.measurements.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

private val Context.measurementPreferencesDataStore by preferencesDataStore(name = "measurement_preferences")

@Singleton
class MeasurementPreferencesStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val lastTypeKey = longPreferencesKey("last_measurement_type")

    suspend fun lastTypeId(): Long? = context.measurementPreferencesDataStore.data.first()[lastTypeKey]

    suspend fun setLastTypeId(id: Long) {
        context.measurementPreferencesDataStore.edit { it[lastTypeKey] = id }
    }
}
