package app.lade.calendar.api.config

data class CalendarDatesConfig(
    val compact: Boolean = false,
    val visibleWeeks: Int = 6,
)

val DefaultCalendarDatesConfig = CalendarDatesConfig()