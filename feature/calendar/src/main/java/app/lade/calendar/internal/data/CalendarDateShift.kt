package app.lade.calendar.internal.data

import app.lade.calendar.internal.domain.mode.CalendarMode
import java.time.LocalDate

internal fun shiftCalendarDate(
    date: LocalDate,
    mode: CalendarMode,
    delta: Long,
): LocalDate = when (mode) {
    CalendarMode.WEEK -> date.plusWeeks(delta)
    CalendarMode.MONTH -> date.plusMonths(delta)
    CalendarMode.YEAR -> date.plusYears(delta)
    CalendarMode.LIST, CalendarMode.TIMELINE -> date
}