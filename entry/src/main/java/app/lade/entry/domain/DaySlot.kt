package app.lade.entry.domain

import app.lade.entry.domain.models.EntryKind
import java.time.Duration
import java.time.LocalTime

data class DaySlot(
    val entryId: Long,
    val historyId: Long? = null,
    val kind: EntryKind,
    val title: String,
    val categoryId: Long,
    val start: LocalTime? = null,
    val end: LocalTime? = null,
    val result: String? = null,
    val fromSeries: Boolean = false,
) {
    val hasTime: Boolean get() = start != null && end != null && end > start
    val duration: Duration
        get() = if (hasTime) Duration.between(start, end) else Duration.ZERO
}
