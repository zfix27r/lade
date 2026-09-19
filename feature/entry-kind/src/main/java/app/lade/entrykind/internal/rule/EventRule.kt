package app.lade.entrykind.internal.rule

import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import javax.inject.Inject

internal class EventRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? {
        val hasTimeRange = input.timeFrom != null && input.timeEnd != null
        return if (hasTimeRange) EntryKind.EVENT else null
    }
}