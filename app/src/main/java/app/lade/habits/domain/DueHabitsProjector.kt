package app.lade.habits.domain

import app.lade.habits.domain.model.Habit
import app.lade.temporal.domain.ScheduleEngine
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Habits due on a local date via RRULE. No busy block is implied.
 */
@Singleton
class DueHabitsProjector @Inject constructor(
	private val scheduleEngine: ScheduleEngine,
) {
	fun project(date: LocalDate, habits: List<Habit>): List<Habit> =
		habits
			.asSequence()
			.filter { scheduleEngine.isDue(it.rrule, date, dtStart = RRULE_ANCHOR) }
			.sortedBy { it.title }
			.toList()

	companion object {
		/** Stable DTSTART until Habit stores its own. Monday 1970-01-05. */
		val RRULE_ANCHOR: LocalDate = LocalDate.of(1970, 1, 5)
	}
}
