package app.lade.calendar.domain

import java.time.LocalDate

data class CalendarWeekUiState(
    val weekStart: LocalDate,
    val days: List<WeekDayRow>,
)
