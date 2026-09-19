package app.lade.entrykind

import java.time.LocalDate
import java.time.LocalTime

data class EntryKindInput(
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val timeFrom: LocalTime? = null,
    val timeEnd: LocalTime? = null,
    val rrule: String? = null,
    val hasGoals: Boolean = false,
)