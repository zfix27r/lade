package app.lade.chat.domain.model

import app.lade.agenda.api.entry.EntryKind

/**
 * User dictionary row (same shape as corpus: kind + phrases → title).
 * System rows live in assets only; Room rows always have [systemKey] = null.
 */
data class ChatDictEntry(
	val id: Long = 0,
	val title: String,
	val kind: EntryKind,
	/** Trigger phrases (lowercase); exact or `*stem*`. */
	val phrases: List<String>,
	val systemKey: String? = null,
	val archivedAtEpochMs: Long? = null,
) {
	val isArchived: Boolean get() = archivedAtEpochMs != null
	val isSystem: Boolean get() = !systemKey.isNullOrBlank()
}
