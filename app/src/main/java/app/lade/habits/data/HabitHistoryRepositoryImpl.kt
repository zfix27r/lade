package app.lade.habits.data

import app.lade.database.dao.HabitHistoryDao
import app.lade.habits.domain.HabitHistoryRepository
import app.lade.habits.domain.model.HabitHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitHistoryRepositoryImpl @Inject constructor(
	private val dao: HabitHistoryDao,
) : HabitHistoryRepository {
	override fun observeByDate(epochDay: Long): Flow<List<HabitHistory>> =
		dao.observeByDate(epochDay).map { list -> list.map { it.toDomain() } }

	override fun observeBetween(fromEpochDay: Long, toEpochDay: Long): Flow<List<HabitHistory>> =
		dao.observeBetween(fromEpochDay, toEpochDay).map { list -> list.map { it.toDomain() } }

	override fun observeForHabitBetween(
		habitId: Long,
		fromEpochDay: Long,
		toEpochDay: Long,
	): Flow<List<HabitHistory>> =
		dao.observeForHabitBetween(habitId, fromEpochDay, toEpochDay)
			.map { list -> list.map { it.toDomain() } }

	override suspend fun get(habitId: Long, epochDay: Long): HabitHistory? =
		dao.get(habitId, epochDay)?.toDomain()

	override suspend fun save(history: HabitHistory): Long {
		return if (history.id == 0L) {
			dao.upsert(history.toEntity().copy(id = 0))
		} else {
			dao.update(history.toEntity())
			history.id
		}
	}
}
