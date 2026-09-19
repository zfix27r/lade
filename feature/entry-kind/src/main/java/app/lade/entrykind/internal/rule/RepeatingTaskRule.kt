package app.lade.entrykind.internal.rule

import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import javax.inject.Inject

internal class RepeatingTaskRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? {
        val hasRecurrence = !input.rrule.isNullOrBlank()
        return if (hasRecurrence) EntryKind.TASK else null
    }
}