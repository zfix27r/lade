package app.lade.calendar.internal.data

import app.lade.calendar.internal.timeline.data.TimelineDay
import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate

internal fun buildTimelineDays(
    start: LocalDate,
    end: LocalDate,
    cards: List<CalendarCardModel>,
): List<TimelineDay> {
    val byDate = cards.groupBy { it.date }
    val days = mutableListOf<TimelineDay>()
    var cursor = start
    while (!cursor.isAfter(end)) {
        days += TimelineDay(
            date = cursor,
            entries = byDate[cursor].orEmpty(),
        )
        cursor = cursor.plusDays(1)
    }
    return days
}