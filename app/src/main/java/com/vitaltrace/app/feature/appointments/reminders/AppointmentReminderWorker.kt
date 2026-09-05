package com.vitaltrace.app.feature.appointments.reminders

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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaltrace.app.MainActivity
import com.vitaltrace.app.R
import com.vitaltrace.app.core.presentation.localization.SpanishDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AppointmentReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val appointmentId = inputData.getLong(AppointmentReminderContract.INPUT_APPOINTMENT_ID, -1L)
        val daysBefore = inputData.getInt(AppointmentReminderContract.INPUT_DAYS_BEFORE, -1)
        val scheduledAtValue = inputData.getString(AppointmentReminderContract.INPUT_SCHEDULED_AT)
            ?: return Result.success()
        if (appointmentId < 0 || daysBefore !in AppointmentReminderContract.DAYS_BEFORE) {
            return Result.success()
        }

        val workName = AppointmentReminderContract.workName(appointmentId, daysBefore)
        val store = AppointmentReminderStore(applicationContext)
        if (!store.isActive(workName, scheduledAtValue)) return Result.success()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            store.setActive(workName, scheduledAt = null)
            return Result.success()
        }

        val scheduledAt = runCatching {
            LocalDateTime.parse(scheduledAtValue, APPOINTMENT_FORMATTER)
        }.getOrNull() ?: return Result.success()

        createNotificationChannel()
        val openAppIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            workName.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(
            applicationContext,
            AppointmentReminderContract.CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.appointment_reminder_title))
            .setContentText(message(daysBefore, scheduledAt))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (!store.isActive(workName, scheduledAtValue)) return Result.success()

        NotificationManagerCompat.from(applicationContext)
            .notify(workName.hashCode(), notification)
        store.setActive(workName, scheduledAt = null)
        return Result.success()
    }

    private fun message(daysBefore: Int, scheduledAt: LocalDateTime): String = when (daysBefore) {
        10 -> applicationContext.getString(R.string.appointment_reminder_10_days)
        7 -> applicationContext.getString(R.string.appointment_reminder_7_days)
        5 -> applicationContext.getString(R.string.appointment_reminder_5_days)
        3 -> applicationContext.getString(R.string.appointment_reminder_3_days)
        1 -> applicationContext.getString(
            R.string.appointment_reminder_1_day,
            SpanishDateTime.formatTime(scheduledAt)
        )
        else -> ""
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            AppointmentReminderContract.CHANNEL_ID,
            applicationContext.getString(R.string.appointment_reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = applicationContext.getString(
                R.string.appointment_reminder_channel_description
            )
        }
        applicationContext.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private companion object {
        val APPOINTMENT_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}