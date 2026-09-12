package app.lade.chat.domain.pipeline

/**
 * [ParseDraft] after Entry resolve (0..N variants for disambiguation).
 */
data class ResolvedDraft(
	val draft: ParseDraft,
	val entryId: Long? = null,
	val entryTitle: String? = null,
)
