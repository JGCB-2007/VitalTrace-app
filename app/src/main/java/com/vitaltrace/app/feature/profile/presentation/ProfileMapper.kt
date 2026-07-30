package com.vitaltrace.app.feature.profile.presentation

import com.vitaltrace.app.feature.patient.domain.model.PatientProfile
import javax.inject.Inject

class ProfileMapper @Inject constructor() {
    fun map(profile: PatientProfile): ProfileUserUiModel {
        val name = profile.fullName?.trim()?.takeIf(String::isNotEmpty)
        return ProfileUserUiModel(
            fullName = name ?: "No disponible",
            initials = name?.split(Regex("\\s+"))
                ?.mapNotNull { it.firstOrNull()?.uppercase() }
                ?.take(2)
                ?.joinToString("")
                ?.takeIf(String::isNotEmpty)
                ?: "VT",
            identifier = profile.recordNumber,
            email = profile.email,
            phone = profile.phone,
            dateOfBirth = profile.dateOfBirth,
            age = profile.age,
            gender = profile.gender?.let(::translateGender),
            address = profile.address,
            identificationNumber = profile.identificationNumber,
            emergencyContactName = profile.emergencyContact?.name,
            emergencyContactPhone = profile.emergencyContact?.phone,
            accountStatus = profile.accountStatus,
            administrativeStatus = profile.administrativeStatus
        )
    }

    private fun translateGender(value: String): String = when (value.uppercase()) {
        "MALE", "MASCULINO" -> "Masculino"
        "FEMALE", "FEMENINO" -> "Femenino"
        else -> value
    }
}
