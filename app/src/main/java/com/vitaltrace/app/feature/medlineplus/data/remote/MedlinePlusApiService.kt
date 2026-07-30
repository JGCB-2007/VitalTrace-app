package com.vitaltrace.app.feature.medlineplus.data.remote

import com.vitaltrace.app.feature.medlineplus.data.dto.MedlinePlusResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MedlinePlusApiService {
    @GET("service")
    suspend fun getDiagnosisEducation(
        @Query("mainSearchCriteria.v.cs") codeSystem: String,
        @Query("mainSearchCriteria.v.c") code: String,
        @Query("informationRecipient.languageCode.c") language: String,
        @Query("knowledgeResponseType") responseType: String
    ): MedlinePlusResponseDto
}
