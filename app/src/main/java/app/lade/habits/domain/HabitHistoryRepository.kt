package app.lade.habits.domain

import app.lade.habits.domain.model.HabitHistory
import kotlinx.coroutines.flow.Flow

interface HabitHistoryRepository {
	fun observeByDate(epochDay: Long): Flow<List<HabitHistory>>
	fun observeBetween(fromEpochDay: Long, toEpochDay: Long): Flow<List<HabitHistory>>
	fun observeForHabitBetween(
		habitId: Long,
		fromEpochDay: Long,
		toEpochDay: Long,
	): Flow<List<HabitHistory>>
	suspend fun get(habitId: Long, epochDay: Long): HabitHistory?
	suspend fun save(history: HabitHistory): Long
}
