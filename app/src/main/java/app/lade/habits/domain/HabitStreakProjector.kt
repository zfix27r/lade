package app.lade.habits.domain

import app.lade.habits.domain.model.Habit
import app.lade.habits.domain.model.HabitHistory
import app.lade.habits.domain.model.HabitHistoryResult
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

enum class HabitDayMarkKind {
	DONE,
	SKIPPED,
	MISSED,
	PLANNED,
	NONE,
}

data class HabitStreakPreview(
	val currentStreak: Int,
	val last7: List<HabitDayMarkKind>,
)

/**
 * Streak / last-7 from History + due (RRULE). Contours stay in habits until analytics UI.
 */
@Singleton
class HabitStreakProjector @Inject constructor(
	private val dueHabitsProjector: DueHabitsProjector,
) {
	fun preview(
		habit: Habit,
		today: LocalDate,
		histories: List<HabitHistory>,
	): HabitStreakPreview {
		val byDay = histories.associateBy { it.dateEpochDay }
		val last7 = (6 downTo 0).map { offset ->
			val date = today.minusDays(offset.toLong())
			kindFor(habit, date, today, byDay[date.toEpochDay()])
		}
		return HabitStreakPreview(
			currentStreak = currentStreak(habit, today, byDay),
			last7 = last7,
		)
	}

	private fun currentStreak(
		habit: Habit,
		today: LocalDate,
		byDay: Map<Long, HabitHistory>,
	): Int {
		var streak = 0
		var date = today
		var guard = 0
		while (guard < 400) {
			guard++
			val due = dueHabitsProjector.project(date, listOf(habit)).isNotEmpty()
			if (!due) {
				date = date.minusDays(1)
				continue
			}
			val result = byDay[date.toEpochDay()]?.result
			when {
				result == HabitHistoryResult.DONE -> {
					streak++
					date = date.minusDays(1)
				}
				date == today && result == null -> {
					// today still planned — do not break streak yet
					date = date.minusDays(1)
				}
				else -> break
			}
		}
		return streak
	}

	private fun kindFor(
		habit: Habit,
		date: LocalDate,
		today: LocalDate,
		history: HabitHistory?,
	): HabitDayMarkKind {
		val due = dueHabitsProjector.project(date, listOf(habit)).isNotEmpty()
		if (!due) return HabitDayMarkKind.NONE
		return when (history?.result) {
			HabitHistoryResult.DONE -> HabitDayMarkKind.DONE
			HabitHistoryResult.SKIPPED -> HabitDayMarkKind.SKIPPED
			else -> if (date.isBefore(today)) HabitDayMarkKind.MISSED else HabitDayMarkKind.PLANNED
		}
	}
}
