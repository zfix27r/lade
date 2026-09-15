package app.lade.calendar.domain

import java.time.Duration
import java.time.LocalDate

data class CalendarDayBusy(
    val date: LocalDate,
    val intervals: List<CalendarBusyInterval>,
    val busy: Duration,
    val free: Duration,
)
