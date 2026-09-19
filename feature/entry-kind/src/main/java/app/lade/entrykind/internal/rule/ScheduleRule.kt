package app.lade.entrykind.internal.rule

import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import javax.inject.Inject

internal class ScheduleRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? {
        val hasRange = input.dateFrom != null &&
                input.dateTo != null &&
                input.dateTo.isAfter(input.dateFrom)
        return if (hasRange) EntryKind.SCHEDULE else null
    }
}