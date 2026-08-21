package app.lade.time.domain

import app.lade.temporal.domain.ScheduleEngine
import app.lade.time.domain.model.TimeBlock
import app.lade.time.domain.model.TimeSchedule
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

data class BusyInterval(
	val blockId: Long,
	val scheduleId: Long?,
	val title: String,
	val categoryId: Long,
	val start: LocalTime,
	val end: LocalTime,
	val source: String,
) {
	val duration: Duration get() = Duration.between(start, end)
}

data class DayBusy(
	val date: LocalDate,
	val intervals: List<BusyInterval>,
	val busy: Duration,
	val free: Duration,
)

/**
 * Projects occupancy onto a local date and computes free = 24h − busy.
 * Formula lives here so calendar does not copy it.
 */
@Singleton
class DayBusyProjector @Inject constructor(
	private val scheduleEngine: ScheduleEngine,
) {
	/** Preferred path once [TimeBlock] rows exist (schedule expand + manual). */
	fun projectBlocks(date: LocalDate, blocks: List<TimeBlock>): DayBusy {
		val intervals = blocks
			.asSequence()
			.filter { it.date == date }
			.filter { it.end > it.start }
			.map {
				BusyInterval(
					blockId = it.id,
					scheduleId = it.scheduleId,
					title = it.title?.takeIf { title -> title.isNotBlank() } ?: "",
					categoryId = it.categoryId,
					start = it.start,
					end = it.end,
					source = it.source,
				)
			}
			.sortedWith(compareBy({ it.start }, { it.title }, { it.blockId }))
			.toList()
		return summarize(date, intervals)
	}

	/** Fallback while blocks are not yet expanded for a date. */
	fun project(date: LocalDate, schedules: List<TimeSchedule>): DayBusy {
		val intervals = schedules
			.asSequence()
			.filter { date in it.dateFrom..it.dateTo }
			.filter { it.endTime > it.startTime }
			.filter { scheduleEngine.isDue(it.rrule, date, dtStart = it.dateFrom) }
			.map {
				BusyInterval(
					blockId = 0L,
					scheduleId = it.id,
					title = it.title,
					categoryId = it.categoryId,
					start = it.startTime,
					end = it.endTime,
					source = "schedule",
				)
			}
			.sortedWith(compareBy({ it.start }, { it.title }))
			.toList()
		return summarize(date, intervals)
	}

	private fun summarize(date: LocalDate, intervals: List<BusyInterval>): DayBusy {
		val busy = intervals.fold(Duration.ZERO) { acc, item -> acc + item.duration }
		val free = (DAY - busy).coerceAtLeast(Duration.ZERO)
		return DayBusy(date = date, intervals = intervals, busy = busy, free = free)
	}

	companion object {
		private val DAY: Duration = Duration.ofHours(24)
	}
}
