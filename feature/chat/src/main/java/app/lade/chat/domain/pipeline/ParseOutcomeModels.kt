package app.lade.chat.domain.pipeline

import app.lade.chat.domain.ChatCommand
import app.lade.agenda.api.entry.EntryKind

/** Orchestrator output after resolve + decide. */
sealed class ParseOutcome {
	data class Execute(val command: ChatCommand) : ParseOutcome()

	data class ChooseEntry(
		val promptKey: String,
		val options: List<EntryPick>,
	) : ParseOutcome()

	data class SuggestCreate(val draft: CreateDraft) : ParseOutcome()

	data class Failed(val reason: String) : ParseOutcome()
}

data class EntryPick(
	val entryId: Long,
	val label: String,
	val command: ChatCommand,
)

data class CreateDraft(
	val kind: EntryKind,
	val title: String,
	val command: ChatCommand.Create,
)
