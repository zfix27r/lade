package app.lade.chat.domain.pipeline.stages

import app.lade.chat.domain.model.ChatDictEntry
import app.lade.chat.domain.pipeline.ParseDraft
import app.lade.chat.domain.pipeline.ResolvedDraft
import app.lade.chat.domain.pipeline.UserIntent
import app.lade.entry.domain.models.Entry
import app.lade.entry.domain.models.EntryKind
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveStage @Inject constructor() {
	fun resolve(
		draft: ParseDraft,
		entries: List<Entry>,
		userDicts: List<ChatDictEntry>,
	): List<ResolvedDraft> {
		val intent = draft.intent.intent
		val kind = draft.intent.kindHint ?: return emptyList()
		return when (intent) {
			UserIntent.MARK_DONE, UserIntent.NAME_OR_CREATE ->
				resolveHabit(draft, entries, userDicts)
			UserIntent.CREATE_KIND ->
				resolveByTitle(draft, entries, kind)
			UserIntent.TIMED ->
				resolveByTitle(draft, entries, kind)
			UserIntent.UNCLEAR -> emptyList()
		}
	}

	private fun resolveHabit(
		draft: ParseDraft,
		entries: List<Entry>,
		userDicts: List<ChatDictEntry>,
	): List<ResolvedDraft> {
		val needle = titleNeedle(draft)
		val needles = needlesFor(needle, userDicts)
		val habits = entries.filter { it.kind == EntryKind.HABIT && !it.isArchived }
		val matches = habits.filter { entry ->
			val title = entry.title.lowercase()
			needles.any { n -> title.contains(n) || n.contains(title) }
		}
		return when {
			matches.isEmpty() -> listOf(ResolvedDraft(draft = draft))
			matches.size == 1 -> listOf(
				ResolvedDraft(
					draft = draft,
					entryId = matches.first().id,
					entryTitle = matches.first().title,
				),
			)
			else -> matches.map { entry ->
				ResolvedDraft(
					draft = draft,
					entryId = entry.id,
					entryTitle = entry.title,
				)
			}
		}
	}

	private fun resolveByTitle(
		draft: ParseDraft,
		entries: List<Entry>,
		kind: EntryKind,
	): List<ResolvedDraft> {
		val needle = titleNeedle(draft)
		if (needle.isBlank()) return listOf(ResolvedDraft(draft = draft))
		val matches = entries.filter {
			it.kind == kind &&
				!it.isArchived &&
				(it.title.lowercase().contains(needle) || needle.contains(it.title.lowercase()))
		}
		return when {
			matches.isEmpty() -> listOf(ResolvedDraft(draft = draft))
			matches.size == 1 -> listOf(
				ResolvedDraft(
					draft = draft,
					entryId = matches.first().id,
					entryTitle = matches.first().title,
				),
			)
			else -> matches.map { entry ->
				ResolvedDraft(
					draft = draft,
					entryId = entry.id,
					entryTitle = entry.title,
				)
			}
		}
	}

	private fun titleNeedle(draft: ParseDraft): String {
		val fromTail = draft.tail.title.trim()
		if (fromTail.isNotEmpty()) return fromTail.lowercase()
		return draft.intent.titleHint.orEmpty().lowercase()
	}

	private fun needlesFor(title: String, userDicts: List<ChatDictEntry>): Set<String> {
		val base = title.lowercase()
		val out = mutableSetOf(base)
		for (dict in userDicts.filter { it.kind == EntryKind.HABIT && !it.isArchived }) {
			val phrases = dict.phrases.map { it.lowercase() } + dict.title.lowercase()
			if (base in phrases || phrases.any { base.contains(it) || it.contains(base) }) {
				out += phrases
			}
		}
		return out
	}
}
