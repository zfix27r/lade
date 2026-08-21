package app.lade.time.domain

import app.lade.temporal.domain.ScheduleEngine
import app.lade.time.domain.model.TimeBlock
import app.lade.time.domain.model.TimeBlockSource
import app.lade.time.domain.model.TimeSchedule
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Syncs future [TimeBlock] rows from a [TimeSchedule] template (`source = schedule`).
 * Past blocks are left unchanged.
 */
@Singleton
class ExpandTimeSchedule @Inject constructor(
	private val blockRepository: TimeBlockRepository,
	private val scheduleEngine: ScheduleEngine,
) {
	private val mutex = Mutex()

	suspend fun sync(schedule: TimeSchedule, today: LocalDate = LocalDate.now()) = mutex.withLock {
		require(schedule.id > 0L) { "schedule must be persisted" }
		if (!schedule.endTime.isAfter(schedule.startTime)) {
			blockRepository.deleteByScheduleFrom(schedule.id, today)
			return
		}
		val from = maxOf(today, schedule.dateFrom)
		if (schedule.dateTo.isBefore(from)) {
			blockRepository.deleteByScheduleFrom(schedule.id, from)
			return
		}
		val dueDates = scheduleEngine.occurrencesBetween(
			rrule = schedule.rrule,
			fromInclusive = from,
			toInclusive = schedule.dateTo,
			dtStart = schedule.dateFrom,
		).toSet()
		val existing = blockRepository.getByScheduleFrom(schedule.id, from)
		val existingByDate = existing.associateBy { it.date }
		for (date in dueDates) {
			val block = TimeBlock(
				id = existingByDate[date]?.id ?: 0L,
				date = date,
				start = schedule.startTime,
				end = schedule.endTime,
				categoryId = schedule.categoryId,
				title = schedule.title,
				source = TimeBlockSource.SCHEDULE,
				scheduleId = schedule.id,
			)
			blockRepository.upsertByScheduleDate(block)
		}
		for (block in existing) {
			if (block.date !in dueDates) {
				blockRepository.delete(block.id)
			}
		}
	}

	suspend fun syncAll(schedules: List<TimeSchedule>, today: LocalDate = LocalDate.now()) {
		schedules.forEach { sync(it, today) }
	}
}
