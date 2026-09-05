package com.vitaltrace.app.core.presentation.localization

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Canonical user-facing locale and date/time formatting for VitalTrace.
 *
 * Every human-readable date the app shows must render through here so the whole
 * UI reads in Nicaraguan Spanish (es-NI).
 *
 * Wire / API formats (`yyyy-MM-dd HH:mm:ss`, ISO-8601, …) are deliberately NOT
 * produced here: request payloads and query params keep their own formatters.
 */
object SpanishDateTime {

    /** The single user-facing locale for the whole app. */
    val LOCALE: Locale = Locale.forLanguageTag("es-NI")

    private val API_DATE_TIME: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val DATE: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMM uuuu", LOCALE)

    private val LONG_DATE: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' uuuu", LOCALE)

    private val TIME: DateTimeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", LOCALE)

    /** e.g. "5 sept 2026". */
    fun formatDate(date: LocalDate): String = date.format(DATE).lowercase(LOCALE)

    fun formatDate(dateTime: LocalDateTime): String = formatDate(dateTime.toLocalDate())

    /** e.g. "5 de septiembre de 2026". */
    fun formatLongDate(date: LocalDate): String = date.format(LONG_DATE).lowercase(LOCALE)

    /** e.g. "9:30 a. m.". */
    fun formatTime(time: LocalTime): String = time.format(TIME).lowercase(LOCALE)

    fun formatTime(dateTime: LocalDateTime): String = formatTime(dateTime.toLocalTime())

    /** Parses an API `yyyy-MM-dd HH:mm:ss` timestamp, or `null` when malformed. */
    fun parseApiDateTime(value: String): LocalDateTime? =
        runCatching { LocalDateTime.parse(value.trim(), API_DATE_TIME) }.getOrNull()

    /**
     * Splits an API `yyyy-MM-dd HH:mm:ss` timestamp into a localized
     * `(date, time)` pair. Falls back to a plain space split when the value
     * cannot be parsed so callers never surface an empty string.
     */
    fun formatApiDateTime(value: String): Pair<String, String> {
        val parsed = parseApiDateTime(value)
            ?: return value.substringBefore(" ") to value.substringAfter(" ", "")
        return formatDate(parsed) to formatTime(parsed)
    }
}
