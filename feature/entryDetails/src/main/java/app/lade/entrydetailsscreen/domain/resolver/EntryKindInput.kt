package app.lade.entrydetailsscreen.domain.resolver

import app.lade.schedule.data.TemporalOptions
import app.lade.recurrence.api.RecurrencePreset

data class EntryKindInput(
    val temporal: TemporalOptions,
    val hasGoals: Boolean,
    val goalsExplicitlyEnabled: Boolean,
) {
    val hasRecurrence: Boolean get() = temporal.recurrence.hasValidDays() &&
            temporal.recurrence.preset != RecurrencePreset.None
    val hasDateRange: Boolean get() = temporal.dateTo != null && temporal.dateFrom != null &&
            temporal.dateTo != temporal.dateFrom
    val hasTimeRange: Boolean get() = temporal.time != null && temporal.timeEnd != null
}