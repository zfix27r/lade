package app.lade.calendar.domain

import CalendarBusySource
import java.time.Duration
import java.time.LocalTime

data class CalendarBusyInterval(
    val blockId: Long,
    val entryId: Long,
    val title: String,
    val start: LocalTime,
    val end: LocalTime,
    val source: CalendarBusySource,
) {
    val duration: Duration get() = Duration.between(start, end)
}