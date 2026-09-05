package com.vitaltrace.app.feature.appointments.reminders

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val store: AppointmentReminderStore
) {
    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    @Synchronized
    fun synchronize(appointments: List<Appointment>, isCompleteSnapshot: Boolean) {
        val currentIds = appointments.map(Appointment::id).toSet()

        if (isCompleteSnapshot) {
            (store.knownAppointmentIds() - currentIds).forEach(::cancelAppointment)
        }

        val now = ZonedDateTime.now()
        appointments.forEach { appointment ->
            cancelAppointment(appointment.id)
            AppointmentReminderPlanner.createPlans(appointment, now).forEach { plan ->
                val workName = AppointmentReminderContract.workName(plan.appointmentId, plan.daysBefore)
                val input = Data.Builder()
                    .putLong(AppointmentReminderContract.INPUT_APPOINTMENT_ID, plan.appointmentId)
                    .putInt(AppointmentReminderContract.INPUT_DAYS_BEFORE, plan.daysBefore)
                    .putString(
                        AppointmentReminderContract.INPUT_SCHEDULED_AT,
                        plan.scheduledAt.format(APPOINTMENT_FORMATTER)
                    )
                    .build()
                val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
                    .setInputData(input)
                    .setInitialDelay(plan.delay.toMillis(), TimeUnit.MILLISECONDS)
                    .addTag(AppointmentReminderContract.WORK_TAG)
                    .build()

                store.setActive(workName, plan.scheduledAt.format(APPOINTMENT_FORMATTER))
                workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.REPLACE, request)
            }
        }

        store.updateKnownAppointmentIds(currentIds, replace = isCompleteSnapshot)
    }

    fun cancelAppointment(appointmentId: Long) {
        AppointmentReminderContract.DAYS_BEFORE.forEach { daysBefore ->
            val workName = AppointmentReminderContract.workName(appointmentId, daysBefore)
            store.setActive(workName, scheduledAt = null)
            workManager.cancelUniqueWork(workName)
        }
    }

    fun cancelAll() {
        workManager.cancelAllWorkByTag(AppointmentReminderContract.WORK_TAG)
        store.clear()
    }

    private companion object {
        val APPOINTMENT_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}