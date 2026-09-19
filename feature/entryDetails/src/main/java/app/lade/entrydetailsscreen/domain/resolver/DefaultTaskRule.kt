package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind

object DefaultTaskRule : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? = EntryKind.TASK
}