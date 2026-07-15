package com.eliauk.momentary.core.database

import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DatabaseConvertersTest {
    private val converters = DatabaseConverters()

    @Test
    fun `instant round trips through epoch millis`() {
        val instant = Instant.parse("2026-07-15T08:30:45.123Z")

        val stored = converters.instantToEpochMillis(instant)

        assertEquals(instant, converters.epochMillisToInstant(stored))
    }

    @Test
    fun `local date round trips through iso text`() {
        val date = LocalDate.of(2026, 7, 15)

        val stored = converters.localDateToIso(date)

        assertEquals(date, converters.isoToLocalDate(stored))
    }

    @Test
    fun `nullable values remain null`() {
        assertNull(converters.instantToEpochMillis(null))
        assertNull(converters.epochMillisToInstant(null))
        assertNull(converters.localDateToIso(null))
        assertNull(converters.isoToLocalDate(null))
    }
}
