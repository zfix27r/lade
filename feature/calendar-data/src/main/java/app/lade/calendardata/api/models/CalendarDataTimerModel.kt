package app.lade.calendardata.api.models

data class CalendarDataTimerModel(
    val plannedStartMinutes: Int,
    val durationMinutes: Int,
    val startedAtMs: Long?,
    val actualMinutes: Int?,
)