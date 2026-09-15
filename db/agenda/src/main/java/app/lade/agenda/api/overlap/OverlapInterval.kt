package app.lade.agenda.api.overlap

import java.time.LocalTime

data class OverlapInterval(
    val entryId: Long,
    val start: LocalTime,
    val end: LocalTime,
    val title: String = "",
    val fromSeries: Boolean = false,
)