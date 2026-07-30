package com.vitaltrace.app.feature.medlineplus.domain.repository

import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducation

interface MedlinePlusRepository {
    suspend fun getDiagnosisEducation(cieCode: String, diagnosisName: String, language: String = "es"): Result<DiagnosisEducation>
}
