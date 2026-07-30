package com.vitaltrace.app.feature.medlineplus.domain.usecase

import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducation
import com.vitaltrace.app.feature.medlineplus.domain.repository.MedlinePlusRepository
import javax.inject.Inject

class GetDiagnosisEducationUseCase @Inject constructor(private val repository: MedlinePlusRepository) {
    suspend operator fun invoke(cieCode: String, diagnosisName: String): Result<DiagnosisEducation> =
        repository.getDiagnosisEducation(cieCode, diagnosisName)
}
