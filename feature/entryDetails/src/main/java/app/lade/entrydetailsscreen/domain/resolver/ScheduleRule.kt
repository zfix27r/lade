package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind


/** Диапазон дат (dateFrom != dateTo) → SCHEDULE. */
object ScheduleRule : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? =
        if (input.hasDateRange) EntryKind.SCHEDULE else null
}