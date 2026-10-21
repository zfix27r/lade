package app.lade.calendar.internal.dates

import app.lade.calendardata.api.mark.CalendarDataMarkModel
import java.time.LocalDate

internal data class CalendarDatesDayModel(
    val date: LocalDate,
    val mark: CalendarDataMarkModel?,
)