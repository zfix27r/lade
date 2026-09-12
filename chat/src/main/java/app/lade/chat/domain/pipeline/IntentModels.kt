package app.lade.chat.domain.pipeline

import app.lade.entry.domain.models.EntryKind

/**
 * User intent after normalize (target pipeline). Minimal set for skeleton.
 */
enum class UserIntent {
	/** Past / done: «пробежал…» → mark history. */
	MARK_DONE,
	/** Name / desire: «бег» → create or clarify, not auto-mark. */
	NAME_OR_CREATE,
	/** Timed block. */
	TIMED,
	/** Verb → kind without known Entry. */
	CREATE_KIND,
	UNCLEAR,
	;

	companion object {
		fun fromStorage(value: String): UserIntent = when (value) {
			"mark_done" -> MARK_DONE
			"name_or_create" -> NAME_OR_CREATE
			"timed" -> TIMED
			"create_kind" -> CREATE_KIND
			else -> UNCLEAR
		}
	}
}

data class IntentResult(
	val intent: UserIntent,
	val matchedPhrase: String? = null,
	val restTokens: List<String> = emptyList(),
	val kindHint: EntryKind? = null,
	val titleHint: String? = null,
	val systemKey: String? = null,
)
