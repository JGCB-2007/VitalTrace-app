package com.vitaltrace.app.feature.measurements.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.vitaltrace.app.feature.profile.data.NotificationPreferencesStore
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MeasurementReminderSnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val store = NotificationPreferencesStore(context.applicationContext)
                val minutes = store.settings.first().snoozeMinutes
                store.recordReminderEvent("SNOOZED")
                val request = OneTimeWorkRequestBuilder<MeasurementReminderWorker>()
                    .setInitialDelay(minutes.toLong(), TimeUnit.MINUTES)
                    .setInputData(workDataOf(MeasurementReminderWorker.INPUT_SNOOZED to true))
                    .build()
                WorkManager.getInstance(context).enqueue(request)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
