package app.lade.calendar.internal.data

import app.lade.calendar.internal.main.CalendarMode
import java.time.LocalDate
import java.time.temporal.WeekFields

internal fun calendarRange(
    date: LocalDate,
    mode: CalendarMode,
    weekFields: WeekFields,
): ClosedRange<LocalDate>? = when (mode) {
    CalendarMode.LIST -> date..date
    CalendarMode.WEEK -> {
        val start = date.with(weekFields.dayOfWeek(), 1L)
        start..start.plusDays(6)
    }
    CalendarMode.MONTH, CalendarMode.YEAR, CalendarMode.TIMELINE -> null
}