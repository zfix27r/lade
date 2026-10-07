package app.lade.calendar.internal.appbar.data

import app.lade.calendar.internal.data.buildTimelineDays
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.EntryKind
import junit.framework.TestCase
import org.junit.Test
import java.time.LocalDate
import kotlin.collections.get

class TimelineDaysTest {

    private fun card(
        date: LocalDate,
        id: Long = date.toEpochDay(),
    ): CalendarCardModel = CalendarCardModel(
        date = date,
        entryId = id,
        entryKind = EntryKind.NOTE,
        title = "card-$id",
        timeFrom = null,
        timeTo = null,
        isSeries = false,
        isAlarm = false,
        isVisited = false,
        goals = emptyList(),
        goalsDone = 0,
        goalsTotal = 0,
    )

    @Test
    fun `single day with no cards yields one empty day`() {
        val date = LocalDate.of(2026, 10, 1)
        val result = buildTimelineDays(
            start = date,
            end = date,
            cards = emptyList(),
        )
        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(date, result[0].date)
        TestCase.assertTrue(result[0].entries.isEmpty())
    }

    @Test
    fun `single day with one card yields that card`() {
        val date = LocalDate.of(2026, 10, 1)
        val c = card(date)
        val result = buildTimelineDays(
            start = date,
            end = date,
            cards = listOf(c),
        )
        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(listOf(c), result[0].entries)
    }

    @Test
    fun `three days cards on first and third yields three days`() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 3)
        val c1 = card(start)
        val c3 = card(end)
        val result = buildTimelineDays(
            start = start,
            end = end,
            cards = listOf(c1, c3),
        )
        TestCase.assertEquals(3, result.size)
        TestCase.assertEquals(start, result[0].date)
        TestCase.assertEquals(listOf(c1), result[0].entries)
        TestCase.assertEquals(LocalDate.of(2026, 10, 2), result[1].date)
        TestCase.assertTrue(result[1].entries.isEmpty())
        TestCase.assertEquals(end, result[2].date)
        TestCase.assertEquals(listOf(c3), result[2].entries)
    }

    @Test
    fun `single day with two cards yields both`() {
        val date = LocalDate.of(2026, 10, 1)
        val c1 = card(date, id = 1L)
        val c2 = card(date, id = 2L)
        val result = buildTimelineDays(
            start = date,
            end = date,
            cards = listOf(c1, c2),
        )
        TestCase.assertEquals(1, result.size)
        TestCase.assertEquals(listOf(c1, c2), result[0].entries)
    }

    @Test
    fun `range groups cards by date`() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 2)
        val c1 = card(start, id = 1L)
        val c2 = card(start, id = 2L)
        val c3 = card(end, id = 3L)
        val c4 = card(end, id = 4L)
        val result = buildTimelineDays(
            start = start,
            end = end,
            cards = listOf(c1, c2, c3, c4),
        )
        TestCase.assertEquals(2, result.size)
        TestCase.assertEquals(listOf(c1, c2), result[0].entries)
        TestCase.assertEquals(listOf(c3, c4), result[1].entries)
    }

    @Test
    fun `empty week yields seven empty days`() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 7)
        val result = buildTimelineDays(
            start = start,
            end = end,
            cards = emptyList(),
        )
        TestCase.assertEquals(7, result.size)
        TestCase.assertTrue(result.all { it.entries.isEmpty() })
        TestCase.assertEquals(start, result.first().date)
        TestCase.assertEquals(end, result.last().date)
    }

    @Test
    fun `cards outside range are ignored`() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 3)
        val outside = card(LocalDate.of(2026, 10, 5))
        val result = buildTimelineDays(
            start = start,
            end = end,
            cards = listOf(outside),
        )
        TestCase.assertEquals(3, result.size)
        TestCase.assertTrue(result.all { it.entries.isEmpty() })
    }
}