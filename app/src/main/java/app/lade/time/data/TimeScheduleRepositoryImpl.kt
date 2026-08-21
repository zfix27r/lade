package app.lade.time.data

import app.lade.database.dao.TimeScheduleDao
import app.lade.time.domain.TimeScheduleRepository
import app.lade.time.domain.model.TimeSchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeScheduleRepositoryImpl @Inject constructor(
	private val dao: TimeScheduleDao,
) : TimeScheduleRepository {
	override fun observeActive(): Flow<List<TimeSchedule>> =
		dao.observeActive().map { list -> list.map { it.toDomain() } }

	override suspend fun getById(id: Long): TimeSchedule? =
		dao.getById(id)?.toDomain()

	override suspend fun save(schedule: TimeSchedule): Long {
		return if (schedule.id == 0L) {
			dao.insert(schedule.toEntity().copy(id = 0))
		} else {
			dao.update(schedule.toEntity())
			schedule.id
		}
	}

	override suspend fun delete(id: Long) {
		dao.delete(id)
	}
}
