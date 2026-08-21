package app.lade.time.data

import app.lade.database.dao.TimeBlockDao
import app.lade.time.domain.TimeBlockRepository
import app.lade.time.domain.model.TimeBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeBlockRepositoryImpl @Inject constructor(
	private val dao: TimeBlockDao,
) : TimeBlockRepository {
	override fun observeByDate(date: LocalDate): Flow<List<TimeBlock>> =
		dao.observeByDate(date.toEpochDay()).map { list -> list.map { it.toDomain() } }

	override fun observeBetween(fromInclusive: LocalDate, toInclusive: LocalDate): Flow<List<TimeBlock>> =
		dao.observeBetween(fromInclusive.toEpochDay(), toInclusive.toEpochDay())
			.map { list -> list.map { it.toDomain() } }

	override suspend fun getByDate(date: LocalDate): List<TimeBlock> =
		dao.getByDate(date.toEpochDay()).map { it.toDomain() }

	override suspend fun getBetween(fromInclusive: LocalDate, toInclusive: LocalDate): List<TimeBlock> =
		dao.getBetween(fromInclusive.toEpochDay(), toInclusive.toEpochDay()).map { it.toDomain() }

	override suspend fun getById(id: Long): TimeBlock? =
		dao.getById(id)?.toDomain()

	override suspend fun getByScheduleFrom(scheduleId: Long, fromInclusive: LocalDate): List<TimeBlock> =
		dao.getByScheduleFrom(scheduleId, fromInclusive.toEpochDay()).map { it.toDomain() }

	override suspend fun save(block: TimeBlock): Long {
		return if (block.id == 0L) {
			dao.insert(block.toEntity().copy(id = 0))
		} else {
			dao.update(block.toEntity())
			block.id
		}
	}

	override suspend fun upsertByScheduleDate(block: TimeBlock): Long {
		val scheduleId = requireNotNull(block.scheduleId) { "scheduleId required for upsert" }
		val existing = dao.getByScheduleAndDate(scheduleId, block.date.toEpochDay())
		return if (existing != null) {
			dao.update(block.toEntity().copy(id = existing.id))
			existing.id
		} else {
			dao.insert(block.toEntity().copy(id = 0))
		}
	}

	override suspend fun delete(id: Long) {
		dao.delete(id)
	}

	override suspend fun deleteByScheduleFrom(scheduleId: Long, fromInclusive: LocalDate) {
		dao.deleteByScheduleFrom(scheduleId, fromInclusive.toEpochDay())
	}
}
