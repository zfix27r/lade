package app.lade.chat.domain.pipeline

import app.lade.entry.domain.models.EntryKind

/** One trigger: exact phrase or `*stem*`, optional intent override. */
data class Needle(
	val text: String,
	val intent: UserIntent? = null,
)

/**
 * Unified match row for system corpus + user dict.
 */
data class MatchRule(
	val needles: List<Needle>,
	val kind: EntryKind,
	val title: String,
	val systemKey: String? = null,
	val userDictId: Long? = null,
)

object MatchRules {
	fun fromCorpus(entry: app.lade.chat.domain.model.CorpusEntry): MatchRule = MatchRule(
		needles = entry.needles,
		kind = entry.kind,
		title = entry.title,
		systemKey = entry.systemKey,
	)

	fun fromUserDict(entry: app.lade.chat.domain.model.ChatDictEntry): MatchRule = MatchRule(
		needles = entry.phrases.map { phrase -> Needle(text = phrase) },
		kind = entry.kind,
		title = entry.title,
		userDictId = entry.id.takeIf { it > 0 },
	)

	fun defaultIntentForKind(kind: EntryKind): UserIntent = when (kind) {
		EntryKind.HABIT -> UserIntent.NAME_OR_CREATE
		EntryKind.SCHEDULE -> UserIntent.TIMED
		EntryKind.EVENT, EntryKind.TASK -> UserIntent.CREATE_KIND
	}
}
