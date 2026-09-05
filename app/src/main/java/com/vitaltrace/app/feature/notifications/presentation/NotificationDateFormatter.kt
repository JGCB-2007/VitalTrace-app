package com.vitaltrace.app.feature.notifications.presentation

import com.vitaltrace.app.core.presentation.localization.SpanishDateTime
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

object NotificationDateFormatter {

    fun format(value: String?): String {
        if (value.isNullOrBlank()) return ""
        val dateTime = parse(value) ?: return ""
        val today = LocalDate.now(dateTime.zone)
        return when (dateTime.toLocalDate()) {
            today -> "Hoy"
            today.minusDays(1) -> "Ayer"
            else -> SpanishDateTime.formatDate(dateTime.toLocalDateTime())
        }
    }

    private fun parse(value: String): ZonedDateTime? = runCatching {
        Instant.parse(value).atZone(ZoneId.systemDefault())
    }.recoverCatching {
        OffsetDateTime.parse(value).atZoneSameInstant(ZoneId.systemDefault())
    }.getOrNull()
}



