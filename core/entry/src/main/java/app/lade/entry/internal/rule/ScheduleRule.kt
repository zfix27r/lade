package app.lade.entry.internal.rule

import app.lade.entry.EntryKind
import app.lade.entry.EntryModel
import javax.inject.Inject

internal class ScheduleRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryModel): EntryKind? {
        val hasRange = input.dateFrom != null &&
                input.dateTo != null &&
                input.dateTo.isAfter(input.dateFrom)
        return if (hasRange) EntryKind.SCHEDULE else null
    }
}