package app.lade.entry.domain

import app.lade.entry.domain.models.Entry
import app.lade.entry.domain.models.SeriesEditDraft
import app.lade.entry.domain.models.SeriesEditScope
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveSeriesEdit @Inject constructor(
	private val entryRepository: EntryRepository,
) {
	suspend fun apply(
		seriesEntry: Entry,
		date: LocalDate,
		scope: SeriesEditScope,
		draft: SeriesEditDraft,
	): Long {
		require(seriesEntry.isSeries) { "entry is not a series" }
		return when (scope) {
			SeriesEditScope.WHOLE_SERIES -> {
				val updated = seriesEntry.copy(
					title = draft.title ?: seriesEntry.title,
					startTime = draft.startTime ?: seriesEntry.startTime,
					endTime = draft.endTime ?: seriesEntry.endTime,
					categoryId = draft.categoryId ?: seriesEntry.categoryId,
				)
				entryRepository.save(updated)
			}
			SeriesEditScope.THIS_DAY_ONLY -> {
				val single = seriesEntry.copy(
					id = 0,
					title = draft.title ?: seriesEntry.title,
					dateFrom = date,
					dateTo = date,
					startTime = draft.startTime ?: seriesEntry.startTime,
					endTime = draft.endTime ?: seriesEntry.endTime,
					categoryId = draft.categoryId ?: seriesEntry.categoryId,
					rrule = null,
					createdAtEpochMs = System.currentTimeMillis(),
				)
				entryRepository.save(single)
			}
		}
	}
}
