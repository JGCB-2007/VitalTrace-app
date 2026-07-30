package com.vitaltrace.app.feature.medlineplus.data.repository

import com.vitaltrace.app.feature.medlineplus.data.mapper.toDomain
import com.vitaltrace.app.feature.medlineplus.data.remote.MedlinePlusApiService
import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducation
import com.vitaltrace.app.feature.medlineplus.domain.repository.MedlinePlusRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedlinePlusRepositoryImpl @Inject constructor(
    private val apiService: MedlinePlusApiService
) : MedlinePlusRepository {
    private val cache = ConcurrentHashMap<String, DiagnosisEducation>()

    override suspend fun getDiagnosisEducation(
        cieCode: String,
        diagnosisName: String,
        language: String
    ): Result<DiagnosisEducation> = runCatching {
        val normalizedCode = cieCode.trim()
        require(normalizedCode.isNotEmpty())
        val key = "${normalizedCode.uppercase()}|${language.lowercase()}"
        cache[key] ?: apiService.getDiagnosisEducation(
            codeSystem = ICD_10_CM_SYSTEM,
            code = normalizedCode,
            language = language,
            responseType = JSON_RESPONSE_TYPE
        ).toDomain(normalizedCode, diagnosisName, language).also { cache[key] = it }
    }

    private companion object {
        const val ICD_10_CM_SYSTEM = "2.16.840.1.113883.6.90"
        const val JSON_RESPONSE_TYPE = "application/json"
    }
}
