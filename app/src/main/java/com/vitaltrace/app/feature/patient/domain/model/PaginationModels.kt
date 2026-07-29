package com.vitaltrace.app.feature.patient.domain.model

data class Page<T>(
    val items: List<T>,
    val links: PaginationLinks,
    val meta: PaginationMeta
)

data class PaginationLinks(
    val first: String,
    val last: String,
    val previous: String?,
    val next: String?
)

data class PaginationMeta(
    val currentPage: Int,
    val from: Int?,
    val lastPage: Int,
    val links: List<PaginationMetaLink>,
    val path: String,
    val perPage: Int,
    val to: Int?,
    val total: Int
)

data class PaginationMetaLink(
    val url: String?,
    val label: String,
    val active: Boolean
)
