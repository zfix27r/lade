package app.lade.calendar.domain

import app.lade.calendardata.api.CalendarCardModel
import java.time.LocalDate

data class TimelineDay(
    val date: LocalDate,
    val entries: List<CalendarCardModel>,
)