package app.lade.habits.domain

import app.lade.habits.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
	fun observeActive(): Flow<List<Habit>>
	suspend fun getById(id: Long): Habit?
	suspend fun save(habit: Habit): Long
	suspend fun delete(id: Long)
}
