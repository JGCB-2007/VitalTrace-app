package com.vitaltrace.app.feature.appointments.reminders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AppointmentNotificationPermissionEffect(
    syncViewModel: AppointmentReminderSyncViewModel = hiltViewModel()
) {
    LaunchedEffect(syncViewModel) { /* Instantiation starts the one-shot appointment sync. */ }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        val alreadyRequested = preferences.getBoolean(KEY_PERMISSION_REQUESTED, false)
        if (!granted && !alreadyRequested) {
            preferences.edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private const val PREFERENCES_NAME = "appointment_reminder_permission"
private const val KEY_PERMISSION_REQUESTED = "requested"