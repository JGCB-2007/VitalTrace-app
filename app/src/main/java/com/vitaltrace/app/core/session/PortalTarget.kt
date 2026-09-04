package com.vitaltrace.app.core.session

/**
 * Android portals a user can enter after authentication.
 *
 * Ordered to match the stable display order used by the portal selector:
 * PATIENT, RELATIVE, NURSE.
 */
enum class PortalTarget {
    PATIENT,
    RELATIVE,
    NURSE
}

/**
 * Resolves which Android portals are available for the given set of roles.
 *
 * Roles without an Android portal (DOCTOR, ADMISSION, SYSTEM_ADMIN, UNKNOWN) are ignored.
 * The result always preserves the stable display order PATIENT, RELATIVE, NURSE.
 *
 * This is the single source of truth for portal-resolution rules and is shared by
 * fresh login ([com.vitaltrace.app.feature.auth.presentation.LoginViewModel]) and
 * session restore ([com.vitaltrace.app.feature.splash.presentation.SplashViewModel]).
 */
fun Set<UserRole>.availablePortals(): List<PortalTarget> = buildList {
    if (UserRole.PATIENT in this@availablePortals) add(PortalTarget.PATIENT)
    if (UserRole.RELATIVE in this@availablePortals) add(PortalTarget.RELATIVE)
    if (UserRole.NURSE in this@availablePortals) add(PortalTarget.NURSE)
}

/**
 * Applies the shared post-authentication routing rule to a resolved portal list:
 * - no Android portal  -> [onNone] (preserves the app's existing fallback)
 * - exactly one portal  -> [onSingle] with that portal (navigate directly)
 * - two or more portals -> [onMultiple] (show the portal selector)
 */
inline fun <T> List<PortalTarget>.resolvePostAuthDestination(
    onNone: () -> T,
    onSingle: (PortalTarget) -> T,
    onMultiple: () -> T
): T = when (size) {
    0 -> onNone()
    1 -> onSingle(first())
    else -> onMultiple()
}
