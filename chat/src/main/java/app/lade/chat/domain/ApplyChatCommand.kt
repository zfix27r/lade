package app.lade.chat.domain

import app.lade.categories.domain.CategoryRepository
import app.lade.entry.domain.EntryRepository
import app.lade.entry.domain.MarkEntryDay
import app.lade.entry.domain.models.Entry
import app.lade.entry.data.EntryHistoryResult
import app.lade.entry.domain.models.EntryKind
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

sealed class ApplyResult {
	data class Ok(val messageKey: String, val detail: String) : ApplyResult()
	data class Err(
		val messageKey: String,
		val createTitle: String? = null,
		val createKind: String? = null,
	) : ApplyResult()
}

/**
 * Apply [ChatCommand] through entry.domain only.
 */
@Singleton
class ApplyChatCommand @Inject constructor(
	private val entryRepository: EntryRepository,
	private val markEntryDay: MarkEntryDay,
	private val categoryRepository: CategoryRepository,
) {
	suspend fun apply(command: ChatCommand): ApplyResult = when (command) {
		is ChatCommand.MarkHistory -> applyMark(command)
		is ChatCommand.UpsertEntry -> applyUpsert(command)
		is ChatCommand.TimedBlock -> applyTimed(command)
		is ChatCommand.Create -> ApplyResult.Err(
			messageKey = "habit_missing",
			createTitle = command.title,
			createKind = command.kind.storage,
		)
	}

	private suspend fun applyMark(command: ChatCommand.MarkHistory): ApplyResult {
		markEntryDay.mark(
			entryId = command.entryId,
			date = command.date,
			result = EntryHistoryResult.DONE,
			actuals = command.actuals,
			startTime = command.startTime,
		)
		val detail = buildString {
			append(command.label)
			command.actuals.forEach { a ->
				append(" · ")
				a.value?.let { append(it) }
				a.unit?.let { append(' ').append(it) }
				if (a.key != "value") append(" ").append(a.key)
			}
		}
		return ApplyResult.Ok("habit", detail)
	}

	private suspend fun applyUpsert(command: ChatCommand.UpsertEntry): ApplyResult {
		val categories = categoryRepository.observeActive().first()
		val categoryId = categories.firstOrNull()?.id
			?: return ApplyResult.Err("no_category")
		val existingId = command.entryId
		val base = existingId?.let { entryRepository.getById(it) }
		val entry = (base ?: Entry(
			kind = command.kind,
			title = command.title,
			categoryId = categoryId,
			createdAtEpochMs = System.currentTimeMillis(),
		)).copy(
			title = command.title,
			categoryId = categoryId,
			kind = command.kind,
			dateFrom = command.date,
			dateTo = command.date,
			startTime = command.startTime,
			endTime = command.endTime,
			rrule = null,
		)
		entryRepository.save(entry)
		val detail = buildString {
			append(command.title)
			command.startTime?.let { start ->
				append(' ').append(start)
				command.endTime?.let { append('–').append(it) }
			}
		}
		return ApplyResult.Ok(
			if (command.kind == EntryKind.TASK) "task" else "block",
			detail,
		)
	}

	private suspend fun applyTimed(command: ChatCommand.TimedBlock): ApplyResult {
		val categories = categoryRepository.observeActive().first()
		val categoryId = categories.firstOrNull()?.id
			?: return ApplyResult.Err("no_category")
		val existingId = command.entryId
		val base = existingId?.let { entryRepository.getById(it) }
		val entry = (base ?: Entry(
			kind = command.kind,
			title = command.title,
			categoryId = categoryId,
			createdAtEpochMs = System.currentTimeMillis(),
		)).copy(
			title = command.title,
			categoryId = categoryId,
			kind = command.kind,
			dateFrom = command.date,
			dateTo = command.date,
			startTime = command.startTime,
			endTime = command.endTime,
			rrule = null,
		)
		entryRepository.save(entry)
		val detail = buildString {
			append(command.title)
			append(' ').append(command.startTime)
			append('–').append(command.endTime)
		}
		return ApplyResult.Ok("block", detail)
	}
}
