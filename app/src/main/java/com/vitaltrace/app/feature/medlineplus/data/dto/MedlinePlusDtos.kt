package com.vitaltrace.app.feature.medlineplus.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MedlinePlusResponseDto(val feed: MedlinePlusFeedDto? = null)

@Serializable
data class MedlinePlusFeedDto(
    val lang: String? = null,
    val entry: List<MedlinePlusEntryDto> = emptyList()
)

@Serializable
data class MedlinePlusEntryDto(
    val title: MedlinePlusValueDto? = null,
    val summary: MedlinePlusValueDto? = null,
    val link: List<MedlinePlusLinkDto> = emptyList()
)

@Serializable
data class MedlinePlusValueDto(@SerialName("_value") val value: String? = null)

@Serializable
data class MedlinePlusLinkDto(val href: String? = null, val rel: String? = null)
