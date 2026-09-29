package app.lade.entry

import java.time.LocalDate
import java.time.LocalTime

data class EntryModel(
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val timeFrom: LocalTime? = null,
    val timeTo: LocalTime? = null,
    val rrule: String? = null,
    val hasGoals: Boolean
)