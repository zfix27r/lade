package app.lade.draftdata

import app.lade.entry.EntryKind
import java.time.LocalDate
import java.time.LocalTime

data class DraftModel(
    val entryId: Long? = null,
    val raw: String? = null,
    val titleRaw: String? = null,
    val dateRaw: String? = null,
    val timeRaw: String? = null,
    val rruleRaw: String? = null,
    val title: String = "",
    val entryKind: EntryKind? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val timeFrom: LocalTime? = null,
    val timeEnd: LocalTime? = null,
    val rrule: String? = null,
    val goals: List<DraftGoal> = emptyList(),
    val notes: List<DraftNote> = emptyList(),
    val alarms: List<DraftAlarm> = emptyList(),
    val reminders: List<DraftReminder> = emptyList(),
) {
    val isEmpty: Boolean
        get() = title.isBlank() &&
                dateFrom == null &&
                timeFrom == null &&
                rrule == null &&
                goals.isEmpty() &&
                alarms.isEmpty() &&
                reminders.isEmpty()

    val isNotEmpty = !this.isEmpty
}