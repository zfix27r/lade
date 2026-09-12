package app.lade.entry.domain.models

import java.time.LocalTime

data class TimedIntervalRef(
    val entryId: Long,
    val start: LocalTime,
    val end: LocalTime,
    val title: String = "",
    val fromSeries: Boolean = false,
)