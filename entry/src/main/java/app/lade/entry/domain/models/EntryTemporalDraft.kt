package app.lade.entry.domain.models

import app.lade.temporal.domain.RecurrenceDraft
import java.time.LocalDate
import java.time.LocalTime

data class EntryTemporalDraft(
    val date: LocalDate? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val time: LocalTime? = null,
    val timeEnd: LocalTime? = null,
    val recurrence: RecurrenceDraft = RecurrenceDraft(),
)