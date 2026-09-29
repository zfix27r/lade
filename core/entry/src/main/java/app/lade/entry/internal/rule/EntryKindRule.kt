package app.lade.entry.internal.rule

import app.lade.entry.EntryKind
import app.lade.entry.EntryModel

internal interface EntryKindRule {
    fun resolve(input: EntryModel): EntryKind?
}