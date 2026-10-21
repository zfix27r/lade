package app.lade.calendar.internal.dates

internal data class CalendarDatesWeekModel(
    val key: CalendarDatesWeekKey,
    val days: List<CalendarDatesDayModel>,
)