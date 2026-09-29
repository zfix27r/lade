package app.lade.chat.internal.data

import app.lade.entry.EntryKind

internal data class CorpusEntry(
    val systemKey: String,
    val entryKind: EntryKind,
    val title: String,
    val needles: List<CorpusNeedle>,
)