package app.lade.chat.domain

import app.lade.entry.domain.models.EntryActual
import app.lade.entry.domain.models.EntryKind
import java.time.LocalDate
import java.time.LocalTime

/**
 * Resolved action for [ApplyChatCommand].
 */
sealed class ChatCommand {
	abstract val label: String

	data class MarkHistory(
		val entryId: Long,
		val date: LocalDate,
		val startTime: LocalTime? = null,
		val actuals: List<EntryActual> = emptyList(),
		override val label: String,
	) : ChatCommand()

	data class UpsertEntry(
		val entryId: Long?,
		val kind: EntryKind,
		val title: String,
		val date: LocalDate,
		val startTime: LocalTime? = null,
		val endTime: LocalTime? = null,
		override val label: String,
	) : ChatCommand()

	data class TimedBlock(
		val entryId: Long?,
		val kind: EntryKind,
		val title: String,
		val date: LocalDate,
		val startTime: LocalTime,
		val endTime: LocalTime,
		override val label: String,
	) : ChatCommand()

	/** Draft when Entry does not exist yet (suggest-create flow). */
	data class Create(
		val kind: EntryKind,
		val title: String,
		val date: LocalDate,
		val startTime: LocalTime? = null,
		val endTime: LocalTime? = null,
		val actuals: List<EntryActual> = emptyList(),
		override val label: String,
	) : ChatCommand()
}
