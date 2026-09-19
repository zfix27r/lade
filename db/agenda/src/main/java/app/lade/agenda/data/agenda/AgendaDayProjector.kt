package app.lade.agenda.data.agenda

import app.lade.agenda.api.entry.EntryModel
import app.lade.entrykind.EntryKind
import app.lade.temporal.api.RecurrenceEngine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgendaDayProjector @Inject constructor(
    private val recurrenceEngine: RecurrenceEngine,
) {
    fun appliesTo(entry: EntryModel, date: LocalDate): Boolean {
        return when (entry.kind) {
            EntryKind.SCHEDULE -> appliesSchedule(entry, date)
            EntryKind.HABIT -> appliesHabit(entry, date)
            EntryKind.TASK -> appliesTask(entry, date)
            EntryKind.EVENT -> appliesEvent(entry, date)
            EntryKind.UNKNOWN -> false
        }
    }

    private fun appliesSchedule(entry: EntryModel, date: LocalDate): Boolean {
        val from = entry.dateFrom ?: return false
        if (date.isBefore(from)) return false
        entry.dateTo?.let { if (date.isAfter(it)) return false }
        val rrule = entry.rrule ?: return false
        return recurrenceEngine.isDue(rrule, date, dtStart = from)
    }

    private fun appliesHabit(entry: EntryModel, date: LocalDate): Boolean {
        val rrule = entry.rrule ?: return false
        val anchor = entry.dateFrom ?: RRULE_ANCHOR
        return recurrenceEngine.isDue(rrule, date, dtStart = anchor)
    }

    private fun appliesTask(entry: EntryModel, date: LocalDate): Boolean {
        val due = entry.dateFrom ?: return false
        if (date.isBefore(due)) return false
        entry.dateTo?.let { if (date.isAfter(it)) return false }
        val rrule = entry.rrule
        if (rrule.isNullOrBlank()) return true
        return recurrenceEngine.isDue(rrule, date, dtStart = due)
    }

    private fun appliesEvent(entry: EntryModel, date: LocalDate): Boolean {
        val day = entry.dateFrom ?: return false
        if (!entry.isSeries) return date == day
        val rrule = entry.rrule ?: return false
        if (date.isBefore(day)) return false
        entry.dateTo?.let { if (date.isAfter(it)) return false }
        return recurrenceEngine.isDue(rrule, date, dtStart = day)
    }

    companion object {
        val RRULE_ANCHOR: LocalDate = LocalDate.of(1970, 1, 5)
    }
}