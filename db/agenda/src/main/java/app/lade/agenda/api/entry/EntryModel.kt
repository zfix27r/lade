package app.lade.agenda.api.entry

import app.lade.entrykind.EntryKind
import java.time.LocalDate
import java.time.LocalTime

data class EntryModel(
    val id: Long = 0,
    val kind: EntryKind,
    val title: String,
    val templateId: Long? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val rrule: String? = null,
    val alarmMode: String = "none",
    val reminderMinutesBefore: Int? = null,
    val archivedAtEpochMs: Long? = null,
    val pausedAtEpochMs: Long? = null,
    val createdAtEpochMs: Long = 0,
    val updatedAtEpochMs: Long? = null,
) {
	val isArchived: Boolean get() = archivedAtEpochMs != null
	val isPaused: Boolean get() = pausedAtEpochMs != null
	val isSeries: Boolean get() = !rrule.isNullOrBlank()
}