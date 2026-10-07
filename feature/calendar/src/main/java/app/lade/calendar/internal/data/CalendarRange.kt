package app.lade.calendar.internal.data

import app.lade.calendar.internal.domain.mode.CalendarMode
import java.time.LocalDate
import java.time.temporal.WeekFields

internal fun calendarRange(
    date: LocalDate,
    mode: CalendarMode,
    weekFields: WeekFields,
): ClosedRange<LocalDate>? = when (mode) {
    CalendarMode.WEEK -> {
        val start = date.with(weekFields.dayOfWeek(), 1L)
        start..start.plusDays(6)
    }
    CalendarMode.MONTH -> {
        val from = date.withDayOfMonth(1)
        from..from.plusMonths(1).minusDays(1)
    }
    CalendarMode.YEAR -> {
        val from = date.withDayOfYear(1)
        from..from.plusYears(1).minusDays(1)
    }
    CalendarMode.LIST, CalendarMode.TIMELINE -> null
}