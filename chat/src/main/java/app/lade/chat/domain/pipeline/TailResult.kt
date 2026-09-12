package app.lade.chat.domain.pipeline

import app.lade.entry.domain.models.EntryActual
import java.time.LocalDate
import java.time.LocalTime

/** Date/time/title/actuals after intent match and slot extraction. */
data class TailResult(
	val date: LocalDate,
	val startTime: LocalTime? = null,
	val endTime: LocalTime? = null,
	val durationMin: Int? = null,
	val title: String,
	val actuals: List<EntryActual> = emptyList(),
)
