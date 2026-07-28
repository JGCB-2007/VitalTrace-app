package com.vitaltrace.app.feature.auth.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    val id: Int,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("middle_name")
    val middleName: String? = null,

    @SerialName("first_last_name")
    val firstLastName: String,

    @SerialName("second_last_name")
    val secondLastName: String? = null,

    @SerialName("date_of_birth")
    val dateOfBirth: String? = null,

    val gender: String? = null,

    @SerialName("identity_document")
    val identityDocument: String? = null,

    val phone: String? = null,
    val address: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)