package app.lade.calendar.internal.dates

import java.time.LocalDate

internal data class CalendarDatesDayModel(
    val date: LocalDate,
    val mark: CalendarDataMinModel?,
)