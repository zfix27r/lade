package app.lade.agenda.domain.models.entry

import app.lade.agenda.api.entry.EntryKind
import app.lade.agenda.api.entry.EntryMissingField
import app.lade.temporal.domain.RecurrenceDraft
import java.time.LocalDate
import java.time.LocalTime

object EntryValidateUseCase {
    fun firstMissing(
        kind: EntryKind,
        title: String,
        templateId: Long?,
        temporal: EntryTemporalDraft,
    ): EntryMissingField? {
        if (title.isBlank()) return EntryMissingField.TITLE
        if (templateId == null) return EntryMissingField.TEMPLATE
        return when (kind) {
            EntryKind.TASK -> {
                val date = temporal.date ?: temporal.dateFrom
                if (date == null) EntryMissingField.DATE else null
            }

            EntryKind.EVENT -> {
                val date = temporal.date ?: temporal.dateFrom
                when {
                    date == null -> EntryMissingField.DATE
                    temporal.time == null || temporal.timeEnd == null -> EntryMissingField.TIME_RANGE
                    temporal.timeEnd <= temporal.time -> EntryMissingField.TIME_RANGE
                    else -> null
                }
            }

            EntryKind.HABIT -> {
                val rrule = temporal.recurrence.toRrule()
                when {
                    rrule.isBlank() -> EntryMissingField.RECURRENCE
                    !temporal.recurrence.hasValidDays() -> EntryMissingField.RECURRENCE
                    else -> null
                }
            }

            EntryKind.SCHEDULE -> {
                when {
                    temporal.time == null || temporal.timeEnd == null -> EntryMissingField.TIME_RANGE
                    temporal.timeEnd <= temporal.time -> EntryMissingField.TIME_RANGE
                    temporal.recurrence.toRrule().isBlank() -> EntryMissingField.RECURRENCE
                    !temporal.recurrence.hasValidDays() -> EntryMissingField.RECURRENCE
                    else -> null
                }
            }

            EntryKind.UNKNOWN -> null
        }
    }

    fun defaultsFor(
        kind: EntryKind,
        anchorDate: LocalDate = LocalDate.now(),
    ): EntryTemporalDraft = when (kind) {
        EntryKind.TASK -> EntryTemporalDraft(date = anchorDate, dateFrom = anchorDate)
        EntryKind.EVENT -> EntryTemporalDraft(
            date = anchorDate,
            dateFrom = anchorDate,
            time = LocalTime.of(9, 0),
            timeEnd = LocalTime.of(10, 0),
        )

        EntryKind.HABIT -> EntryTemporalDraft(recurrence = RecurrenceDraft())
        EntryKind.SCHEDULE -> EntryTemporalDraft(
            dateFrom = anchorDate,
            time = LocalTime.of(9, 0),
            timeEnd = LocalTime.of(18, 0),
            recurrence = RecurrenceDraft(),
        )

        EntryKind.UNKNOWN -> EntryTemporalDraft(recurrence = RecurrenceDraft())
    }
}