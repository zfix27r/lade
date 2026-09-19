package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind


/** Повтор без goal → TASK (повторяющаяся задача). */
object RepeatingTaskRule : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? =
        if (input.hasRecurrence) EntryKind.TASK else null
}