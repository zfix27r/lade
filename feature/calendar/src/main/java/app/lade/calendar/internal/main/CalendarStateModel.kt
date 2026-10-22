package app.lade.calendar.internal.main

import app.lade.calendardata.api.models.CalendarDataModel
import java.time.LocalDate

internal data class CalendarStateModel(
    val mode: CalendarMode,
    val currentDate: LocalDate,
    val days: List<CalendarDataModel>,
    val marks: Map<LocalDate, CalendarDataMinModel>,
)