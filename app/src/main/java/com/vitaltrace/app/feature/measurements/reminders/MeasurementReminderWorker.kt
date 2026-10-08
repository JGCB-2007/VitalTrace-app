package com.vitaltrace.app.feature.measurements.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaltrace.app.MainActivity
import com.vitaltrace.app.R
import com.vitaltrace.app.feature.profile.data.NotificationPreferencesStore
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class MeasurementReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val settingsStore = NotificationPreferencesStore(applicationContext)
        val settings = settingsStore.settings.first()
        val isSnoozed = inputData.getBoolean(INPUT_SNOOZED, false)
        if (!settings.measurementRemindersEnabled) return Result.success()
        if (!isSnoozed && LocalDate.now().dayOfWeek.value !in settings.reminderDays) {
            return Result.success()
        }
        if (settings.lastReminderEvent in setOf("SENT", "SNOOZED_SENT") &&
            settings.lastReminderAt?.let { System.currentTimeMillis() - it > 6 * 60 * 60 * 1000L } == true
        ) {
            settingsStore.recordReminderEvent("MISSED")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                applicationContext.getString(R.string.measurement_reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = applicationContext.getString(R.string.measurement_reminder_channel_description)
            }
        )

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            data = "vitaltrace://measurement-form".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            7301,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val snoozeIntent = Intent(applicationContext, MeasurementReminderSnoozeReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            7302,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.measurement_reminder_title))
            .setContentText(applicationContext.getString(R.string.measurement_reminder_message))
            .setContentIntent(pendingIntent)
            .addAction(0, applicationContext.getString(R.string.measurement_reminder_snooze), snoozePendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(7301, notification)
        settingsStore.recordReminderEvent(if (isSnoozed) "SNOOZED_SENT" else "SENT")
        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "measurement_reminders"
        const val INPUT_SNOOZED = "snoozed"
    }
}
