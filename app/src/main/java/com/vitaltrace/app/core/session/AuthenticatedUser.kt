package com.vitaltrace.app.core.session

data class AuthenticatedUser(
    val id: Int,
    val email: String,
    val personId: Int,
    val fullName: String,
    val roles: Set<UserRole>
)

enum class UserRole {
    PATIENT,
    RELATIVE,
    DOCTOR,
    NURSE,
    ADMISSION,
    SYSTEM_ADMIN,
    UNKNOWN;

    companion object {
        fun fromApiValue(value: String): UserRole =
            entries.firstOrNull { it != UNKNOWN && it.name == value.uppercase() } ?: UNKNOWN
    }
}
