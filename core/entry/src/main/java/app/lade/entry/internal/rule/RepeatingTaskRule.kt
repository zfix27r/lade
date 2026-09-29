package app.lade.entry.internal.rule

import app.lade.entry.EntryKind
import app.lade.entry.EntryModel
import javax.inject.Inject

internal class RepeatingTaskRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryModel): EntryKind? {
        val hasRecurrence = !input.rrule.isNullOrBlank()
        return if (hasRecurrence) EntryKind.TASK else null
    }
}