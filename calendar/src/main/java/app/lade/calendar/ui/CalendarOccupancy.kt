package app.lade.calendar.ui

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/** Occupancy source keys (aligned with FeedSource where applicable). */
object CalendarBusySource {
	const val SCHEDULE = "schedule"
	const val MANUAL = "manual"
	const val HEALTH = "health"
	const val CALENDAR = "calendar"
}

data class CalendarBusyInterval(
	val blockId: Long,
	val entryId: Long,
	val title: String,
	val categoryId: Long,
	val start: LocalTime,
	val end: LocalTime,
	val source: String,
) {
	val duration: Duration get() = Duration.between(start, end)
}

data class CalendarDayBusy(
	val date: LocalDate,
	val intervals: List<CalendarBusyInterval>,
	val busy: Duration,
	val free: Duration,
)

data class DueHabitItem(
	val entryId: Long,
	val title: String,
	val categoryId: Long,
	val goalLabel: String?,
	val timeOfDayMinutes: Int?,
	val result: String?,
)
