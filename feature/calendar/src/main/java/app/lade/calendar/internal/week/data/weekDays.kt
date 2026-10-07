package app.lade.calendar.internal.week.data

import java.time.LocalDate
import java.time.temporal.WeekFields

internal fun weekDays(
    date: LocalDate,
    weekFields: WeekFields,
): List<LocalDate> {
    val weekStart = date.with(weekFields.dayOfWeek(), 1L)
    return (0L..6L).map { weekStart.plusDays(it) }
}