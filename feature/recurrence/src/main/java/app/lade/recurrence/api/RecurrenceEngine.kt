package app.lade.recurrence.api

import java.time.LocalDate

interface RecurrenceEngine {
    fun isDue(rrule: String, date: LocalDate, dtStart: LocalDate = date): Boolean

    fun occurrencesBetween(
        rrule: String,
        fromInclusive: LocalDate,
        toInclusive: LocalDate,
        dtStart: LocalDate = fromInclusive,
    ): List<LocalDate>
}