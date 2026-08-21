package app.lade.time.domain

import app.lade.time.domain.model.TimeBlock
import app.lade.time.domain.model.TimeBlockSource
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Creates or updates a one-off busy interval (`source = manual`, no scheduleId).
 * Validates end > start, no overlaps, and sum ≤ 24h. Times snap to 5-minute steps.
 */
@Singleton
class SaveManualTimeBlock @Inject constructor(
	private val blockRepository: TimeBlockRepository,
) {
	suspend fun save(
		id: Long = 0,
		date: LocalDate,
		start: LocalTime,
		end: LocalTime,
		categoryId: Long,
		title: String?,
	): Long {
		val snappedStart = start.snapToFiveMinutes()
		val snappedEnd = end.snapToFiveMinutes()
		require(snappedEnd.isAfter(snappedStart)) { "end must be after start" }

		val others = blockRepository.getByDate(date).filter { it.id != id }
		require(others.none { overlaps(snappedStart, snappedEnd, it.start, it.end) }) {
			"block overlaps another on the same day"
		}
		val busy = others.fold(Duration.ZERO) { acc, block ->
			acc + Duration.between(block.start, block.end)
		} + Duration.between(snappedStart, snappedEnd)
		require(busy <= DAY) { "busy must be ≤ 24h" }

		return blockRepository.save(
			TimeBlock(
				id = id,
				date = date,
				start = snappedStart,
				end = snappedEnd,
				categoryId = categoryId,
				title = title?.trim()?.takeIf { it.isNotEmpty() },
				source = TimeBlockSource.MANUAL,
				scheduleId = null,
			),
		)
	}

	suspend fun delete(id: Long) {
		val existing = blockRepository.getById(id) ?: return
		require(existing.source == TimeBlockSource.MANUAL) { "only manual blocks can be deleted here" }
		blockRepository.delete(id)
	}

	private fun overlaps(
		aStart: LocalTime,
		aEnd: LocalTime,
		bStart: LocalTime,
		bEnd: LocalTime,
	): Boolean = aStart < bEnd && bStart < aEnd

	companion object {
		private val DAY: Duration = Duration.ofHours(24)
	}
}

private fun LocalTime.snapToFiveMinutes(): LocalTime {
	val total = hour * 60 + minute
	val snapped = (total / 5) * 5
	return LocalTime.ofSecondOfDay(snapped * 60L)
}

