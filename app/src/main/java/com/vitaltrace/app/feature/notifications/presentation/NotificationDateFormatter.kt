package com.vitaltrace.app.feature.notifications.presentation

import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object NotificationDateFormatter {
    private val locale = Locale.forLanguageTag("es-NI")
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", locale)
    private val dateFormatter = DateTimeFormatter.ofPattern("d MMM uuuu", locale)

    fun format(value: String?): String {
        if (value.isNullOrBlank()) return ""
        val dateTime = parse(value) ?: return ""
        val today = LocalDate.now(dateTime.zone)
        return when (dateTime.toLocalDate()) {
            today -> "Hoy"
            today.minusDays(1) -> "Ayer"
            else -> dateTime.format(dateFormatter).lowercase(locale)
        }
    }

    private fun parse(value: String): ZonedDateTime? = runCatching {
        Instant.parse(value).atZone(ZoneId.systemDefault())
    }.recoverCatching {
        OffsetDateTime.parse(value).atZoneSameInstant(ZoneId.systemDefault())
    }.getOrNull()
}



