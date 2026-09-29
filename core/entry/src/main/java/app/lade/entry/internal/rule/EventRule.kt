package app.lade.entry.internal.rule

import app.lade.entry.EntryKind
import app.lade.entry.EntryModel
import javax.inject.Inject

internal class EventRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryModel): EntryKind? {
        val hasTimeRange = input.timeFrom != null && input.timeTo != null
        return if (hasTimeRange) EntryKind.EVENT else null
    }
}