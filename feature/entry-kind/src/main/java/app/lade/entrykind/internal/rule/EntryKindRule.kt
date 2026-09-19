package app.lade.entrykind.internal.rule

import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput

internal interface EntryKindRule {
    fun resolve(input: EntryKindInput): EntryKind?
}