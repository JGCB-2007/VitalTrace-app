package com.vitaltrace.app.feature.appointments.reminders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import com.vitaltrace.app.R

@Composable
fun AppointmentNotificationPermissionEffect(
    syncViewModel: AppointmentReminderSyncViewModel = hiltViewModel()
) {
    val notificationsEnabled by syncViewModel.notificationsEnabled.collectAsStateWithLifecycle()
    LaunchedEffect(syncViewModel) { /* Instantiation starts the one-shot appointment sync. */ }

    if (notificationsEnabled != true) return

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    var showRationale by rememberSaveable { mutableStateOf(false) }
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
            showRationale = true
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = {
                preferences.edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
                showRationale = false
            },
            title = { Text(stringResource(R.string.notification_permission_title)) },
            text = { Text(stringResource(R.string.notification_permission_description)) },
            confirmButton = {
                TextButton(onClick = {
                    preferences.edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
                    showRationale = false
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text(stringResource(R.string.notification_permission_enable)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    preferences.edit().putBoolean(KEY_PERMISSION_REQUESTED, true).apply()
                    showRationale = false
                }) { Text(stringResource(R.string.notification_permission_later)) }
            }
        )
    }
}

private const val PREFERENCES_NAME = "appointment_reminder_permission"
private const val KEY_PERMISSION_REQUESTED = "requested"
