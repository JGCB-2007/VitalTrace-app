package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(notificationId: Long): Result<PatientNotification> =
        repository.markNotificationAsRead(notificationId)
}
