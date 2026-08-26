package com.vitaltrace.app.feature.auth.data.mapper

import com.vitaltrace.app.core.session.AuthenticatedUser
import com.vitaltrace.app.core.session.UserRole
import com.vitaltrace.app.feature.auth.data.remote.dto.UserDto

fun UserDto.toAuthenticatedUser(): AuthenticatedUser {
    val fullName = listOf(
        person.firstName,
        person.middleName,
        person.firstLastName,
        person.secondLastName
    ).filterNotNull().filter(String::isNotBlank).joinToString(" ")

    return AuthenticatedUser(
        id = id,
        email = email,
        personId = personId,
        fullName = fullName,
        roles = roles.map { UserRole.fromApiValue(it.name) }.toSet()
    )
}
