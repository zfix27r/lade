package app.lade.calendar.internal.dates

import java.time.LocalDate
import java.time.temporal.WeekFields

internal data class CalendarDatesWeekKey(
    val year: Int,
    val week: Int,
) {
    val asString: String get() = "$year-$week"

    companion object {
        fun of(date: LocalDate): CalendarDatesWeekKey =
            CalendarDatesWeekKey(
                year = date.get(WeekFields.ISO.weekBasedYear()),
                week = date.get(WeekFields.ISO.weekOfWeekBasedYear()),
            )
    }
}