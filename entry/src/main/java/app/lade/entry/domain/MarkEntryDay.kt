package app.lade.entry.domain

import app.lade.entry.domain.models.EntryHistory
import app.lade.entry.data.EntryHistoryResult
import app.lade.entry.domain.models.EntryKind
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarkEntryDay @Inject constructor(
	private val entryRepository: EntryRepository,
	private val historyRepository: EntryHistoryRepository,
) {
	suspend fun mark(
		entryId: Long,
		date: LocalDate,
		result: String,
		actuals: List<app.lade.entry.domain.models.EntryActual> = emptyList(),
		startTime: java.time.LocalTime? = null,
	): String? {
		val entry = entryRepository.getById(entryId) ?: return null
		require(entry.kind == EntryKind.HABIT) { "mark only for habit" }
		val existing = historyRepository.observeByDate(date).first()
			.find { it.entryId == entryId }
		val previous = existing?.result
		val now = System.currentTimeMillis()
		historyRepository.save(
			EntryHistory(
				id = existing?.id ?: 0L,
				entryId = entry.id,
				kind = EntryKind.HABIT,
				title = entry.title,
				categoryId = entry.categoryId,
				date = date,
				startTime = startTime ?: entry.startTime,
				endTime = entry.endTime,
				result = result,
				goals = entry.goalDefs,
				actuals = actuals.ifEmpty { existing?.actuals.orEmpty() },
				notedAtEpochMs = now,
				source = "chat",
			),
		)
		return previous
	}

	suspend fun restore(entryId: Long, date: LocalDate, previousResult: String?) {
		val entry = entryRepository.getById(entryId) ?: return
		val existing = historyRepository.observeByDate(date).first()
			.find { it.entryId == entryId } ?: return
		if (previousResult == null) {
			historyRepository.save(
				existing.copy(result = null, notedAtEpochMs = System.currentTimeMillis()),
			)
		} else {
			mark(entryId, date, previousResult)
		}
	}
}