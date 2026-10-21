package app.lade.calendar.api.config

data class MonthConfig(
    val mode: CalendarMonthMode = CalendarMonthMode.FLOW,
    val compact: Boolean = false,
    val monthsBefore: Long = 12,
    val monthsAfter: Long = 12,
)

val DefaultMonthConfig = MonthConfig()