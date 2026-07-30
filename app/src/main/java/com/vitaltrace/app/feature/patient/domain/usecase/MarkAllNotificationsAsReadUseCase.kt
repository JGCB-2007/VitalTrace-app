package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.MarkAllNotificationsReadResult
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(): Result<MarkAllNotificationsReadResult> =
        repository.markAllNotificationsAsRead()
}
