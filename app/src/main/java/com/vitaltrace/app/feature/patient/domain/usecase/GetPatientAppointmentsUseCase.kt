package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.appointments.reminders.AppointmentReminderScheduler
import com.vitaltrace.app.feature.patient.domain.model.Appointment
import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetPatientAppointmentsUseCase @Inject constructor(
    private val patientRepository: PatientRepository,
    private val appointmentReminderScheduler: AppointmentReminderScheduler? = null
) {

    suspend operator fun invoke(page: Int? = null, forceRefresh: Boolean = false): Result<Page<Appointment>> {
        return patientRepository.getAppointments(page = page, forceRefresh = forceRefresh).onSuccess { appointmentsPage ->
            appointmentReminderScheduler?.synchronize(
                appointments = appointmentsPage.items,
                isCompleteSnapshot = appointmentsPage.meta.currentPage == 1 &&
                    appointmentsPage.meta.lastPage == 1
            )
        }
    }
}
