package app.lade.chat.internal.data

import app.lade.entrykind.EntryKind

internal data class CorpusEntry(
    val systemKey: String,
    val kind: EntryKind,
    val title: String,
    val needles: List<CorpusNeedle>,
)