package com.vitaltrace.app.core.presentation.localization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class SpanishDateTimeTest {

    @Test fun `canonical locale is Nicaraguan Spanish`() {
        assertEquals("es", SpanishDateTime.LOCALE.language)
        assertEquals("NI", SpanishDateTime.LOCALE.country)
    }

    @Test fun `formatDate renders a Spanish month abbreviation`() {
        val text = SpanishDateTime.formatDate(LocalDate.of(2026, 9, 5)).lowercase()
        assertTrue(text, text.contains("2026"))
        assertTrue(text, text.startsWith("5"))
        // Spanish, not English month spelling.
        assertFalse(text, text.contains("september"))
        assertFalse(text, text.contains("december"))
    }

    @Test fun `formatLongDate spells the month in Spanish`() {
        val text = SpanishDateTime.formatLongDate(LocalDate.of(2026, 1, 3))
        assertTrue(text, text.contains("enero"))
        assertTrue(text, text.contains("de"))
    }

    @Test fun `formatTime uses a 12h clock`() {
        val text = SpanishDateTime.formatTime(LocalTime.of(21, 30))
        assertTrue(text, text.contains("9:30"))
    }

    @Test fun `parseApiDateTime accepts the wire format and rejects junk`() {
        assertNotNull(SpanishDateTime.parseApiDateTime("2026-09-05 09:30:00"))
        assertNull(SpanishDateTime.parseApiDateTime("05/09/2026"))
        assertNull(SpanishDateTime.parseApiDateTime(""))
    }

    @Test fun `formatApiDateTime splits into localized date and time`() {
        val (date, time) = SpanishDateTime.formatApiDateTime("2026-09-05 09:30:00")
        assertTrue(date, date.contains("2026"))
        assertTrue(time, time.contains("9:30"))
    }

    @Test fun `formatApiDateTime degrades gracefully for an unparseable value`() {
        val (date, time) = SpanishDateTime.formatApiDateTime("2026-09-05")
        assertEquals("2026-09-05", date)
        assertEquals("", time)
    }
}
