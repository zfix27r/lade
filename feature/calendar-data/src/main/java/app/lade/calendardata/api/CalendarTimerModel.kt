package app.lade.calendardata.api

data class CalendarTimerModel(
    val plannedStartMinutes: Int,
    val durationMinutes: Int,
    val startedAtMs: Long?,
    val actualMinutes: Int?,
)