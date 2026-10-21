package app.lade.calendar.internal.dates.grid

import app.lade.calendar.internal.dates.CalendarDatesDayModel
import app.lade.calendar.internal.dates.CalendarDatesWeekKey
import app.lade.calendar.internal.dates.CalendarDatesWeekModel
import app.lade.calendardata.api.mark.CalendarDataMarkModel
import java.time.LocalDate
import java.time.temporal.WeekFields

internal fun buildCalendarDatesWeeks(
    from: LocalDate,
    to: LocalDate,
    marks: Map<LocalDate, CalendarDataMarkModel>,
    weekFields: WeekFields,
): List<CalendarDatesWeekModel> {
    val start = from.with(weekFields.dayOfWeek(), 1L)
    val weeks = mutableListOf<CalendarDatesWeekModel>()
    var cursor = start
    while (!cursor.isAfter(to)) {
        val days = (0L..6L).map { offset ->
            val date = cursor.plusDays(offset)
            CalendarDatesDayModel(
                date = date,
                mark = marks[date],
            )
        }
        weeks += CalendarDatesWeekModel(
            key = CalendarDatesWeekKey.of(cursor),
            days = days,
        )
        cursor = cursor.plusWeeks(1)
    }
    return weeks
}