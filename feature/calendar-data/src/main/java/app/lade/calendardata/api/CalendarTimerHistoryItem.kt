package app.lade.calendardata.api

data class CalendarTimerHistoryItem(
    val id: Long,
    val startedAtMs: Long,
    val finishedAtMs: Long?,
    val actualMinutes: Int?,
    val plannedMinutes: Int,
)