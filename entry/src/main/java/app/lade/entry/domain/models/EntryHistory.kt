package app.lade.entry.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class EntryHistory(
	val id: Long = 0,
	val entryId: Long? = null,
	val kind: EntryKind,
	val title: String,
	val categoryId: Long,
	val date: LocalDate,
	val startTime: LocalTime? = null,
	val endTime: LocalTime? = null,
	val result: String? = null,
	val goals: List<EntryGoalDef> = emptyList(),
	val actuals: List<EntryActual> = emptyList(),
	val notedAtEpochMs: Long = 0,
	val source: String? = null,
)
