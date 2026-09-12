package app.lade.entry.data

import app.lade.database.entry.EntryHistoryDao
import app.lade.entry.domain.EntryHistoryRepository
import app.lade.entry.domain.models.EntryHistory
import app.lade.entry.domain.models.EntryKind
import app.lade.entry.domain.models.EntryMissingField
import app.lade.entry.domain.models.EntryTemporalDraft
import app.lade.temporal.domain.RecurrenceDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryHistoryRepositoryImpl @Inject constructor(
	private val dao: EntryHistoryDao,
) : EntryHistoryRepository {
	override fun observeByDate(date: LocalDate): Flow<List<EntryHistory>> =
		dao.observeByDate(date.toEpochDay()).map { list -> list.map { it.toDomain() } }

	override fun observeBetween(
		fromInclusive: LocalDate,
		toInclusive: LocalDate,
	): Flow<List<EntryHistory>> =
		dao.observeBetween(fromInclusive.toEpochDay(), toInclusive.toEpochDay())
			.map { list -> list.map { it.toDomain() } }

	override suspend fun save(history: EntryHistory): Long {
		return if (history.id == 0L) {
			dao.insert(history.toEntity().copy(id = 0))
		} else {
			dao.insert(history.toEntity())
		}
	}
}

object EntryCreateMatrix {
    fun firstMissing(
		kind: EntryKind,
	    title: String,
	    categoryId: Long?,
	    temporal: EntryTemporalDraft,
    ): EntryMissingField? {
        if (title.isBlank()) return EntryMissingField.TITLE
        if (categoryId == null) return EntryMissingField.CATEGORY
        return when (kind) {
            EntryKind.TASK -> {
                val date = temporal.date ?: temporal.dateFrom
                if (date == null) EntryMissingField.DATE else null
            }
            EntryKind.EVENT -> {
                val date = temporal.date ?: temporal.dateFrom
                when {
                    date == null -> EntryMissingField.DATE
                    temporal.time == null || temporal.timeEnd == null -> EntryMissingField.TIME_RANGE
                    temporal.timeEnd!! <= temporal.time!! -> EntryMissingField.TIME_RANGE
                    else -> null
                }
            }
            EntryKind.HABIT -> {
                val rrule = temporal.recurrence.toRrule()
                when {
                    rrule.isBlank() -> EntryMissingField.RECURRENCE
                    !temporal.recurrence.hasValidDays() -> EntryMissingField.RECURRENCE
                    else -> null
                }
            }
            EntryKind.SCHEDULE -> {
                when {
                    temporal.time == null || temporal.timeEnd == null -> EntryMissingField.TIME_RANGE
                    temporal.timeEnd!! <= temporal.time!! -> EntryMissingField.TIME_RANGE
                    temporal.recurrence.toRrule().isBlank() -> EntryMissingField.RECURRENCE
                    !temporal.recurrence.hasValidDays() -> EntryMissingField.RECURRENCE
                    else -> null
                }
            }
        }
    }

    fun defaultsFor(
		kind: EntryKind,
	    anchorDate: LocalDate = LocalDate.now(),
    ): EntryTemporalDraft = when (kind) {
        EntryKind.TASK -> EntryTemporalDraft(date = anchorDate, dateFrom = anchorDate)
        EntryKind.EVENT -> EntryTemporalDraft(
			date = anchorDate,
			dateFrom = anchorDate,
			time = LocalTime.of(9, 0),
			timeEnd = LocalTime.of(10, 0),
		)
        EntryKind.HABIT -> EntryTemporalDraft(recurrence = RecurrenceDraft())
        EntryKind.SCHEDULE -> EntryTemporalDraft(
			dateFrom = anchorDate,
			time = LocalTime.of(9, 0),
			timeEnd = LocalTime.of(18, 0),
			recurrence = RecurrenceDraft(),
		)
    }
}