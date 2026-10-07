package app.lade.calendar

import app.lade.calendar.internal.data.shiftCalendarDate
import app.lade.calendar.internal.domain.mode.CalendarMode
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalendarDateShiftTest {

    @Test
    fun `week forward adds seven days`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 10, 1),
            mode = CalendarMode.WEEK,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2026, 10, 8), result)
    }

    @Test
    fun `week backward subtracts seven days`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 10, 1),
            mode = CalendarMode.WEEK,
            delta = -1L,
        )
        assertEquals(LocalDate.of(2026, 9, 24), result)
    }

    @Test
    fun `month forward keeps day when valid`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 10, 15),
            mode = CalendarMode.MONTH,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2026, 11, 15), result)
    }

    @Test
    fun `month forward clamps day to shorter month`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 1, 31),
            mode = CalendarMode.MONTH,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2026, 2, 28), result)
    }

    @Test
    fun `month forward clamps day to leap february`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2024, 1, 31),
            mode = CalendarMode.MONTH,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2024, 2, 29), result)
    }

    @Test
    fun `month backward clamps day to shorter month`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 3, 31),
            mode = CalendarMode.MONTH,
            delta = -1L,
        )
        assertEquals(LocalDate.of(2026, 2, 28), result)
    }

    @Test
    fun `year forward keeps day when valid`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 6, 15),
            mode = CalendarMode.YEAR,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2027, 6, 15), result)
    }

    @Test
    fun `year forward clamps february 29 to february 28`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2024, 2, 29),
            mode = CalendarMode.YEAR,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2025, 2, 28), result)
    }

    @Test
    fun `year backward keeps day`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 6, 15),
            mode = CalendarMode.YEAR,
            delta = -1L,
        )
        assertEquals(LocalDate.of(2025, 6, 15), result)
    }

    @Test
    fun `list mode does not shift date`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 10, 15),
            mode = CalendarMode.LIST,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2026, 10, 15), result)
    }

    @Test
    fun `timeline mode does not shift date`() {
        val result = shiftCalendarDate(
            date = LocalDate.of(2026, 10, 15),
            mode = CalendarMode.TIMELINE,
            delta = 1L,
        )
        assertEquals(LocalDate.of(2026, 10, 15), result)
    }
}