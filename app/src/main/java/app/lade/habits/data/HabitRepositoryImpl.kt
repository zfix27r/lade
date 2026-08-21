package app.lade.habits.data

import app.lade.database.dao.HabitDao
import app.lade.habits.domain.HabitRepository
import app.lade.habits.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
	private val dao: HabitDao,
) : HabitRepository {
	override fun observeActive(): Flow<List<Habit>> =
		dao.observeActive().map { list -> list.map { it.toDomain() } }

	override suspend fun getById(id: Long): Habit? =
		dao.getById(id)?.toDomain()

	override suspend fun save(habit: Habit): Long {
		return if (habit.id == 0L) {
			dao.insert(habit.toEntity(System.currentTimeMillis()).copy(id = 0))
		} else {
			val existing = dao.getById(habit.id)
			val created = existing?.createdAtEpochMs ?: System.currentTimeMillis()
			dao.update(habit.toEntity(created))
			habit.id
		}
	}

	override suspend fun delete(id: Long) {
		dao.delete(id)
	}
}
