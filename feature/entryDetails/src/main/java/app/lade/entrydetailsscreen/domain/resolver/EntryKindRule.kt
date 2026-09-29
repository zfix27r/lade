package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entry.EntryKind

interface EntryKindRule {
    fun resolve(input: EntryKindInput): EntryKind?
}