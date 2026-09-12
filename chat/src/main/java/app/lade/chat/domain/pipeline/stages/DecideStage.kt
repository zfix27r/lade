package app.lade.chat.domain.pipeline.stages

import app.lade.chat.domain.ChatCommand
import app.lade.chat.domain.pipeline.CreateDraft
import app.lade.chat.domain.pipeline.EntryPick
import app.lade.chat.domain.pipeline.ParseOutcome
import app.lade.chat.domain.pipeline.ResolvedDraft
import app.lade.chat.domain.pipeline.UserIntent
import app.lade.entry.domain.models.EntryKind
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecideStage @Inject constructor() {
	fun decide(resolved: List<ResolvedDraft>): ParseOutcome {
		if (resolved.isEmpty()) return ParseOutcome.Failed("unclear")
		val draft = resolved.first().draft
		if (draft.intent.intent == UserIntent.UNCLEAR) {
			return ParseOutcome.Failed("unclear")
		}
		return when (draft.intent.intent) {
			UserIntent.MARK_DONE -> decideMark(resolved)
			UserIntent.NAME_OR_CREATE -> decideSuggest(resolved)
			UserIntent.CREATE_KIND -> decideUpsert(resolved)
			UserIntent.TIMED -> decideTimed(resolved)
			UserIntent.UNCLEAR -> ParseOutcome.Failed("unclear")
		}
	}

	private fun decideMark(resolved: List<ResolvedDraft>): ParseOutcome {
		val withEntry = resolved.filter { it.entryId != null }
		when {
			withEntry.size == 1 -> return ParseOutcome.Execute(
				toMarkCommand(withEntry.first()),
			)
			withEntry.size > 1 -> return ParseOutcome.ChooseEntry(
				promptKey = "choice_which_entry",
				options = withEntry.map { toEntryPick(it, toMarkCommand(it)) },
			)
		}
		return ParseOutcome.SuggestCreate(toCreateDraft(resolved.first()))
	}

	private fun decideSuggest(resolved: List<ResolvedDraft>): ParseOutcome {
		val withEntry = resolved.filter { it.entryId != null }
		if (withEntry.size > 1) {
			return ParseOutcome.ChooseEntry(
				promptKey = "choice_which_entry",
				options = withEntry.map {
					toEntryPick(it, toCreateCommand(it))
				},
			)
		}
		return ParseOutcome.SuggestCreate(toCreateDraft(resolved.first()))
	}

	private fun decideUpsert(resolved: List<ResolvedDraft>): ParseOutcome {
		val withEntry = resolved.filter { it.entryId != null }
		when {
			withEntry.size == 1 -> {
				val row = withEntry.first()
				return ParseOutcome.Execute(toUpsertCommand(row))
			}
			withEntry.size > 1 -> return ParseOutcome.ChooseEntry(
				promptKey = "choice_which_entry",
				options = withEntry.map { toEntryPick(it, toUpsertCommand(it)) },
			)
		}
		return ParseOutcome.SuggestCreate(toCreateDraft(resolved.first()))
	}

	private fun decideTimed(resolved: List<ResolvedDraft>): ParseOutcome {
		val withEntry = resolved.filter { it.entryId != null }
		when {
			withEntry.size == 1 -> {
				val cmd = toTimedCommand(withEntry.first())
					?: return ParseOutcome.Failed("bad_duration")
				return ParseOutcome.Execute(cmd)
			}
			withEntry.size > 1 -> {
				val options = withEntry.mapNotNull { row ->
					toTimedCommand(row)?.let { cmd -> toEntryPick(row, cmd) }
				}
				if (options.isEmpty()) return ParseOutcome.Failed("bad_duration")
				return ParseOutcome.ChooseEntry(
					promptKey = "choice_which_entry",
					options = options,
				)
			}
		}
		val cmd = toTimedCommand(resolved.first())
			?: return ParseOutcome.Failed("bad_duration")
		return ParseOutcome.Execute(cmd)
	}

	private fun toMarkCommand(row: ResolvedDraft): ChatCommand.MarkHistory {
		val draft = row.draft
		val label = row.entryTitle ?: draft.tail.title
		return ChatCommand.MarkHistory(
			entryId = row.entryId!!,
			date = draft.tail.date,
			startTime = draft.tail.startTime,
			actuals = draft.tail.actuals,
			label = label,
		)
	}

	private fun toUpsertCommand(row: ResolvedDraft): ChatCommand.UpsertEntry {
		val draft = row.draft
		val kind = draft.intent.kindHint ?: EntryKind.TASK
		val title = row.entryTitle ?: draft.tail.title.ifBlank {
			draft.intent.titleHint.orEmpty()
		}
		return ChatCommand.UpsertEntry(
			entryId = row.entryId,
			kind = kind,
			title = title,
			date = draft.tail.date,
			startTime = draft.tail.startTime,
			endTime = draft.tail.endTime,
			label = title,
		)
	}

	private fun toTimedCommand(row: ResolvedDraft): ChatCommand.TimedBlock? {
		val draft = row.draft
		val kind = draft.intent.kindHint ?: EntryKind.SCHEDULE
		val start = draft.tail.startTime ?: return null
		val end = draft.tail.endTime ?: return null
		if (end <= start) return null
		val title = row.entryTitle ?: draft.tail.title.ifBlank {
			draft.intent.titleHint.orEmpty()
		}
		return ChatCommand.TimedBlock(
			entryId = row.entryId,
			kind = kind,
			title = title,
			date = draft.tail.date,
			startTime = start,
			endTime = end,
			label = "$title $start",
		)
	}

	private fun toCreateCommand(row: ResolvedDraft): ChatCommand.Create =
		toCreateDraft(row).command

	private fun toCreateDraft(row: ResolvedDraft): CreateDraft {
		val draft = row.draft
		val kind = draft.intent.kindHint ?: EntryKind.HABIT
		val title = draft.tail.title.ifBlank {
			draft.intent.titleHint.orEmpty()
		}.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
		val command = ChatCommand.Create(
			kind = kind,
			title = title,
			date = draft.tail.date,
			startTime = draft.tail.startTime,
			endTime = draft.tail.endTime,
			actuals = draft.tail.actuals,
			label = title,
		)
		return CreateDraft(kind = kind, title = title, command = command)
	}

	private fun toEntryPick(row: ResolvedDraft, command: ChatCommand): EntryPick =
		EntryPick(
			entryId = row.entryId!!,
			label = row.entryTitle ?: draftLabel(row),
			command = command,
		)

	private fun draftLabel(row: ResolvedDraft): String =
		row.entryTitle ?: row.draft.tail.title.ifBlank {
			row.draft.intent.titleHint.orEmpty()
		}
}
