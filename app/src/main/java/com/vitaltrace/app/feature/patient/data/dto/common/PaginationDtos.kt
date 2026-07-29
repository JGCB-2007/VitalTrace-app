package com.vitaltrace.app.feature.patient.data.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponseDto<T>(
    val data: List<T> = emptyList(),
    val links: PaginationLinksDto,
    val meta: PaginationMetaDto
)

@Serializable
data class PaginationLinksDto(
    val first: String,
    val last: String,
    val prev: String? = null,
    val next: String? = null
)

@Serializable
data class PaginationMetaDto(
    @SerialName("current_page") val currentPage: Int,
    val from: Int? = null,
    @SerialName("last_page") val lastPage: Int,
    val links: List<PaginationMetaLinkDto> = emptyList(),
    val path: String,
    @SerialName("per_page") val perPage: Int,
    val to: Int? = null,
    val total: Int
)

@Serializable
data class PaginationMetaLinkDto(
    val url: String? = null,
    val label: String,
    val active: Boolean
)
