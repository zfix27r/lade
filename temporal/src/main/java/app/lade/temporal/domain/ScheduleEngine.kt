package app.lade.temporal.domain

import java.time.LocalDate

/**
 * Port for RRULE evaluation. Callers must not depend on BiweeklyScheduleEngine directly.
 */
interface ScheduleEngine {
	fun isDue(rrule: String, date: LocalDate, dtStart: LocalDate = date): Boolean

	fun occurrencesBetween(
		rrule: String,
		fromInclusive: LocalDate,
		toInclusive: LocalDate,
		dtStart: LocalDate = fromInclusive,
	): List<LocalDate>
}
