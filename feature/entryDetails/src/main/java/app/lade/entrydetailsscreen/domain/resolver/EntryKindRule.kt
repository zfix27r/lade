package app.lade.entrydetailsscreen.domain.resolver

import app.lade.entrykind.EntryKind

interface EntryKindRule {
    fun resolve(input: EntryKindInput): EntryKind?
}