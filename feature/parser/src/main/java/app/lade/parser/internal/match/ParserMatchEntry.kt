package app.lade.parser.internal.match

import app.lade.entry.EntryKind

internal data class ParserMatchEntry(
    val systemKey: String,
    val entryKind: EntryKind,
    val title: String,
    val needles: List<ParserMatchNeedle>,
)