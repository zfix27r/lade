package app.lade.calendar

import app.lade.calendar.internal.data.calendarRange
import app.lade.calendar.internal.domain.mode.CalendarMode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

class CalendarRangeTest {

    private val weekFields = WeekFields.of(Locale.forLanguageTag("ru-RU"))

    @Test
    fun `week range starts on monday and spans seven days`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 10, 1),
            mode = CalendarMode.WEEK,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2026, 9, 28)..LocalDate.of(2026, 10, 4),
            result,
        )
    }

    @Test
    fun `week range for monday starts on that monday`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 9, 28),
            mode = CalendarMode.WEEK,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2026, 9, 28)..LocalDate.of(2026, 10, 4),
            result,
        )
    }

    @Test
    fun `month range covers full month`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 10, 15),
            mode = CalendarMode.MONTH,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2026, 10, 1)..LocalDate.of(2026, 10, 31),
            result,
        )
    }

    @Test
    fun `month range for february leap year`() {
        val result = calendarRange(
            date = LocalDate.of(2024, 2, 10),
            mode = CalendarMode.MONTH,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2024, 2, 1)..LocalDate.of(2024, 2, 29),
            result,
        )
    }

    @Test
    fun `month range for february non leap year`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 2, 10),
            mode = CalendarMode.MONTH,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2026, 2, 1)..LocalDate.of(2026, 2, 28),
            result,
        )
    }

    @Test
    fun `year range covers full year`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 6, 15),
            mode = CalendarMode.YEAR,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2026, 1, 1)..LocalDate.of(2026, 12, 31),
            result,
        )
    }

    @Test
    fun `year range for leap year includes december 31`() {
        val result = calendarRange(
            date = LocalDate.of(2024, 6, 15),
            mode = CalendarMode.YEAR,
            weekFields = weekFields,
        )
        assertEquals(
            LocalDate.of(2024, 1, 1)..LocalDate.of(2024, 12, 31),
            result,
        )
    }

    @Test
    fun `list mode returns null`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 10, 15),
            mode = CalendarMode.LIST,
            weekFields = weekFields,
        )
        assertNull(result)
    }

    @Test
    fun `timeline mode returns null`() {
        val result = calendarRange(
            date = LocalDate.of(2026, 10, 15),
            mode = CalendarMode.TIMELINE,
            weekFields = weekFields,
        )
        assertNull(result)
    }
}