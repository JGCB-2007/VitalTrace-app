package com.vitaltrace.app.feature.measurements.reminders

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.vitaltrace.app.feature.profile.presentation.NotificationSettingsUiModel

@Singleton
class MeasurementReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun apply(settings: NotificationSettingsUiModel) {
        val workManager = WorkManager.getInstance(context)
        if (!settings.measurementRemindersEnabled) {
            workManager.cancelUniqueWork(WORK_NAME)
            return
        }

        val now = ZonedDateTime.now()
        var nextReminder = now.withHour(settings.reminderHour)
            .withMinute(settings.reminderMinute).withSecond(0).withNano(0)
        if (!nextReminder.isAfter(now)) nextReminder = nextReminder.plusDays(1)
        val request = PeriodicWorkRequestBuilder<MeasurementReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(Duration.between(now, nextReminder).toMillis(), TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private companion object {
        const val WORK_NAME = "daily-measurement-reminder"
    }
}
