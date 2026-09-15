package app.lade.calendar.domain

import java.time.DayOfWeek
import java.time.YearMonth

data class CalendarMonthUiState(
    val month: YearMonth,
    val weekdayLabels: List<DayOfWeek>,
    val days: List<MonthDayCell>,
)
