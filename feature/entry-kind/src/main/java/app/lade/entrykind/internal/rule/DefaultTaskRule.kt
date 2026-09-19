package app.lade.entrykind.internal.rule

import app.lade.entrykind.EntryKind
import app.lade.entrykind.EntryKindInput
import javax.inject.Inject

internal class DefaultTaskRule @Inject constructor() : EntryKindRule {
    override fun resolve(input: EntryKindInput): EntryKind = EntryKind.TASK
}