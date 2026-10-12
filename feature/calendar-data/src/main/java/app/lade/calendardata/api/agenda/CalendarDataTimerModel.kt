package app.lade.calendardata.api.agenda

data class CalendarDataTimerModel(
    val plannedStartMinutes: Int,
    val durationMinutes: Int,
    val startedAtMs: Long?,
    val actualMinutes: Int?,
)