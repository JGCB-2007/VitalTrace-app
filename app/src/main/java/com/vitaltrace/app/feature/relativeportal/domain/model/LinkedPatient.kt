package com.vitaltrace.app.feature.relativeportal.domain.model

data class LinkedPatient(
    val id: Long,
    val fullName: String,
    val relationship: String,
    val status: String,
    val avatarUrl: String?
)