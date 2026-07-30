package com.vitaltrace.app.feature.medlineplus.data.mapper

import android.text.Html
import com.vitaltrace.app.feature.medlineplus.data.dto.MedlinePlusResponseDto
import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducation
import com.vitaltrace.app.feature.medlineplus.domain.model.DiagnosisEducationItem

fun MedlinePlusResponseDto.toDomain(cieCode: String, diagnosisName: String, language: String) = DiagnosisEducation(
    cieCode = cieCode,
    diagnosisName = diagnosisName,
    language = feed?.lang?.takeIf(String::isNotBlank) ?: language,
    source = "MedlinePlus",
    items = feed?.entry.orEmpty().mapNotNull { entry ->
        val title = entry.title?.value?.trim().orEmpty()
        val url = entry.link.firstOrNull { it.rel == "alternate" }?.href?.trim().orEmpty()
        if (title.isBlank() || url.isBlank()) return@mapNotNull null
        DiagnosisEducationItem(
            title = title,
            summary = Html.fromHtml(entry.summary?.value.orEmpty(), Html.FROM_HTML_MODE_COMPACT).toString().trim(),
            url = url
        )
    }
)
