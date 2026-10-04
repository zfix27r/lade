package app.lade.calendar.ui.appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.lade.calendar.domain.CalendarMode
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun calendarTitle(
    mode: CalendarMode,
    date: LocalDate,
    visibleMonth: YearMonth,
    locale: Locale,
): String {
    val monthFmt = remember(locale) {
        DateTimeFormatter.ofPattern("LLLL", locale)
    }
    val dayMonthFmt = remember(locale) {
        DateTimeFormatter.ofPattern("d MMM", locale)
    }
    return when (mode) {
        CalendarMode.LIST -> date.format(monthFmt)
        CalendarMode.TIMELINE -> visibleMonth.atDay(1).format(monthFmt)
        CalendarMode.WEEK -> {
            val weekFields = WeekFields.of(locale)
            val start = date.with(weekFields.dayOfWeek(), 1L)
            val end = start.plusDays(6)
            if (start.month == end.month) {
                "${start.dayOfMonth} – ${end.format(dayMonthFmt)}"
            } else {
                "${start.format(dayMonthFmt)} – ${end.format(dayMonthFmt)}"
            }
        }
        CalendarMode.MONTH -> date.format(monthFmt)
        CalendarMode.YEAR -> date.year.toString()
    }
}