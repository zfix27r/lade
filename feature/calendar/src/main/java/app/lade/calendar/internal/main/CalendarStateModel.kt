package app.lade.calendar.internal.main

import app.lade.calendardata.api.agenda.CalendarDataDayModel
import app.lade.calendardata.api.mark.CalendarDataMarkModel
import java.time.LocalDate

internal data class CalendarStateModel(
    val mode: CalendarMode,
    val currentDate: LocalDate,
    val days: List<CalendarDataDayModel>,
    val marks: Map<LocalDate, CalendarDataMarkModel>,
)