package com.vitaltrace.app.feature.patient.domain.usecase

import com.vitaltrace.app.feature.patient.domain.model.Page
import com.vitaltrace.app.feature.patient.domain.model.PatientNotification
import com.vitaltrace.app.feature.patient.domain.repository.PatientRepository
import javax.inject.Inject

class GetPatientNotificationsUseCase @Inject constructor(
    private val repository: PatientRepository
) {
    suspend operator fun invoke(
        read: String = "all",
        type: String? = null,
        page: Int = 1
    ): Result<Page<PatientNotification>> = repository.getNotifications(read, type, page)
}

