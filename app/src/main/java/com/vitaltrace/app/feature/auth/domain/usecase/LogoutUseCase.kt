package com.vitaltrace.app.feature.auth.domain.usecase

import com.vitaltrace.app.core.session.SessionManager
import com.vitaltrace.app.feature.appointments.reminders.AppointmentReminderScheduler
import com.vitaltrace.app.feature.relativeportal.domain.selection.RelativePatientSelection
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val appointmentReminderScheduler: AppointmentReminderScheduler? = null,
    private val relativePatientSelection: RelativePatientSelection? = null
) {

    suspend operator fun invoke(): Result<Unit> {
        return sessionManager.logout().onSuccess {
            appointmentReminderScheduler?.cancelAll()
            relativePatientSelection?.clear()
        }
    }
}
