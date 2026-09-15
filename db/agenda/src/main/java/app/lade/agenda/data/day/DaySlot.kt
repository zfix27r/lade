package app.lade.agenda.data.day

import app.lade.agenda.api.entry.EntryModel
import app.lade.agenda.api.log.LogModel
import java.time.Duration
import java.time.LocalTime

data class DaySlot(
    val entry: EntryModel,
    val logs: List<LogModel> = emptyList(),
    val fromSeries: Boolean = false,
) {
    val entryId: Long get() = entry.id
    val kind get() = entry.kind
    val title get() = entry.title
    val templateId get() = entry.templateId
    val start: LocalTime? get() = entry.startTime
    val end: LocalTime? get() = entry.endTime

    val hasTime: Boolean
        get() {
            val s = entry.startTime ?: return false
            val e = entry.endTime ?: return false
            return e > s
        }

    val duration: Duration
        get() {
            val s = entry.startTime ?: return Duration.ZERO
            val e = entry.endTime ?: return Duration.ZERO
            return if (e > s) Duration.between(s, e) else Duration.ZERO
        }
}