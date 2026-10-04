package app.lade.calendardata.internal

import app.lade.agendastore.entry.EntryEntity
import app.lade.entry.EntryKind
import app.lade.recurrence.api.RecurrenceEngine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CalendarDayProjector @Inject constructor(
    private val recurrenceEngine: RecurrenceEngine,
) {
    fun appliesTo(entry: EntryEntity, date: LocalDate): Boolean {
        val kind = EntryKind.fromStorage(entry.kind) ?: return false
        return when (kind) {
            EntryKind.NOTE -> appliesNote(entry, date)
            EntryKind.SCHEDULE -> appliesSchedule(entry, date)
            EntryKind.HABIT -> appliesHabit(entry, date)
            EntryKind.TASK -> appliesTask(entry, date)
            EntryKind.EVENT -> appliesEvent(entry, date)
        }
    }

    private fun appliesNote(entry: EntryEntity, date: LocalDate): Boolean {
        val day = entry.dateFromEpochDay?.let(LocalDate::ofEpochDay) ?: return false
        val rrule = entry.rrule
        if (rrule.isNullOrBlank()) return date == day
        if (date.isBefore(day)) return false
        entry.dateToEpochDay?.let { if (date.isAfter(LocalDate.ofEpochDay(it))) return false }
        return recurrenceEngine.isDue(rrule, date, dtStart = day)
    }

    private fun appliesSchedule(entry: EntryEntity, date: LocalDate): Boolean {
        val from = entry.dateFromEpochDay?.let(LocalDate::ofEpochDay) ?: return false
        if (date.isBefore(from)) return false
        entry.dateToEpochDay?.let { if (date.isAfter(LocalDate.ofEpochDay(it))) return false }
        val rrule = entry.rrule ?: return false
        return recurrenceEngine.isDue(rrule, date, dtStart = from)
    }

    private fun appliesHabit(entry: EntryEntity, date: LocalDate): Boolean {
        val rrule = entry.rrule ?: return false
        val anchor = entry.dateFromEpochDay?.let(LocalDate::ofEpochDay) ?: RRULE_ANCHOR
        return recurrenceEngine.isDue(rrule, date, dtStart = anchor)
    }

    private fun appliesTask(entry: EntryEntity, date: LocalDate): Boolean {
        val due = entry.dateFromEpochDay?.let(LocalDate::ofEpochDay) ?: return false
        val rrule = entry.rrule
        if (rrule.isNullOrBlank()) {
            val to = entry.dateToEpochDay?.let(LocalDate::ofEpochDay)
            return if (to == null) {
                date == due
            } else {
                !date.isBefore(due) && !date.isAfter(to)
            }
        }
        if (date.isBefore(due)) return false
        entry.dateToEpochDay?.let { if (date.isAfter(LocalDate.ofEpochDay(it))) return false }
        return recurrenceEngine.isDue(rrule, date, dtStart = due)
    }

    private fun appliesEvent(entry: EntryEntity, date: LocalDate): Boolean {
        val day = entry.dateFromEpochDay?.let(LocalDate::ofEpochDay) ?: return false
        val rrule = entry.rrule
        if (rrule.isNullOrBlank()) return date == day
        if (date.isBefore(day)) return false
        entry.dateToEpochDay?.let { if (date.isAfter(LocalDate.ofEpochDay(it))) return false }
        return recurrenceEngine.isDue(rrule, date, dtStart = day)
    }

    companion object {
        val RRULE_ANCHOR: LocalDate = LocalDate.of(1970, 1, 5)
    }
}