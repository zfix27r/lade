package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entry.EntryKind

object DefaultTaskRule : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind? = EntryKind.TASK
}