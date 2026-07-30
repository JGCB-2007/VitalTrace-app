package com.vitaltrace.app.feature.medlineplus.domain.model

data class DiagnosisEducation(
    val cieCode: String,
    val diagnosisName: String,
    val language: String,
    val source: String,
    val items: List<DiagnosisEducationItem>
)

data class DiagnosisEducationItem(val title: String, val summary: String, val url: String)
