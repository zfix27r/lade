package app.lade.chat.domain.model

import app.lade.chat.domain.pipeline.Needle
import app.lade.entry.domain.models.EntryKind

/** System row from assets JSON (not in Room). */
data class CorpusEntry(
	val systemKey: String,
	val kind: EntryKind,
	val title: String,
	val needles: List<Needle>,
) {
	val isSystem: Boolean = true
}
