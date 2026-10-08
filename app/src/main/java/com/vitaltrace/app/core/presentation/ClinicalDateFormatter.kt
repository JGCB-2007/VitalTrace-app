package com.vitaltrace.app.core.presentation

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private fun parseClinicalDateTime(value: String): LocalDateTime? {
    val normalized = value.trim().replace(' ', 'T')
    return try {
        OffsetDateTime.parse(normalized).toLocalDateTime()
    } catch (_: DateTimeParseException) {
        try {
            LocalDateTime.parse(normalized)
        } catch (_: DateTimeParseException) {
            try {
                LocalDate.parse(normalized).atStartOfDay()
            } catch (_: DateTimeParseException) {
                null
            }
        }
    }
}

fun formatClinicalDate(value: String): String {
    val dateTime = parseClinicalDateTime(value) ?: return value.substringBefore('T').substringBefore(' ')
    return dateTime.format(DateTimeFormatter.ofPattern("d MMM uuuu", Locale.getDefault()))
        .lowercase(Locale.getDefault())
}

fun formatClinicalTime(value: String): String {
    val dateTime = parseClinicalDateTime(value) ?: return value.substringAfter(' ', "")
    if (dateTime.toLocalTime() == java.time.LocalTime.MIDNIGHT && !value.contains(':')) return ""
    return dateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
        .lowercase(Locale.getDefault())
}

fun formatClinicalDateTime(value: String): String {
    val date = formatClinicalDate(value)
    val time = formatClinicalTime(value)
    return listOf(date, time).filter(String::isNotBlank).joinToString(" · ")
}
