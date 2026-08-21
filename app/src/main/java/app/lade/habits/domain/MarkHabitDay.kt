package app.lade.habits.domain

import app.lade.habits.domain.model.Habit
import app.lade.habits.domain.model.HabitHistory
import app.lade.habits.domain.model.HabitHistoryResult
import app.lade.habits.domain.model.HabitHistorySource
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Creates or updates a day snapshot ([HabitHistory]) with done/skipped.
 * Snapshot fields are copied from [Habit] on first mark; later edits stay on History.
 */
@Singleton
class MarkHabitDay @Inject constructor(
	private val historyRepository: HabitHistoryRepository,
) {
	/**
	 * @return previous result before this mark (null if unmarked).
	 */
	suspend fun mark(
		habit: Habit,
		date: LocalDate,
		result: String,
		actualValue: Double? = null,
	): String? {
		require(result == HabitHistoryResult.DONE || result == HabitHistoryResult.SKIPPED) {
			"result must be done or skipped"
		}
		val epochDay = date.toEpochDay()
		val existing = historyRepository.get(habit.id, epochDay)
		val previous = existing?.result
		val now = System.currentTimeMillis()
		val resolvedActual = when {
			actualValue != null -> actualValue
			result == HabitHistoryResult.DONE -> existing?.actualValue ?: habit.goalValue
			else -> existing?.actualValue
		}
		val history = if (existing == null) {
			HabitHistory(
				habitId = habit.id,
				dateEpochDay = epochDay,
				title = habit.title,
				categoryId = habit.categoryId,
				goalType = habit.goalType,
				goalValue = habit.goalValue,
				goalUnit = habit.goalUnit,
				timeOfDayMinutes = habit.timeOfDayMinutes,
				result = result,
				actualValue = if (result == HabitHistoryResult.DONE) resolvedActual else null,
				notedAtEpochMs = now,
				source = HabitHistorySource.MANUAL,
			)
		} else {
			existing.copy(
				result = result,
				actualValue = if (result == HabitHistoryResult.DONE) {
					resolvedActual
				} else {
					null
				},
				notedAtEpochMs = now,
				source = HabitHistorySource.MANUAL,
			)
		}
		historyRepository.save(history)
		return previous
	}

	/** Restores [previousResult] after undo (null clears the mark). */
	suspend fun restore(
		habit: Habit,
		date: LocalDate,
		previousResult: String?,
	) {
		val epochDay = date.toEpochDay()
		val existing = historyRepository.get(habit.id, epochDay) ?: return
		when (previousResult) {
			null -> {
				historyRepository.save(
					existing.copy(
						result = null,
						actualValue = null,
						notedAtEpochMs = null,
					),
				)
			}
			HabitHistoryResult.DONE, HabitHistoryResult.SKIPPED -> {
				mark(habit = habit, date = date, result = previousResult)
			}
		}
	}
}
