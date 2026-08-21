package app.lade.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.lade.database.entity.TimeBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeBlockDao {
	@Query("SELECT * FROM time_blocks WHERE dateEpochDay = :epochDay ORDER BY startTimeMinutes ASC, id ASC")
	fun observeByDate(epochDay: Long): Flow<List<TimeBlockEntity>>

	@Query(
		"""
		SELECT * FROM time_blocks
		WHERE dateEpochDay BETWEEN :fromEpochDay AND :toEpochDay
		ORDER BY dateEpochDay DESC, startTimeMinutes ASC, id ASC
		""",
	)
	fun observeBetween(fromEpochDay: Long, toEpochDay: Long): Flow<List<TimeBlockEntity>>

	@Query("SELECT * FROM time_blocks WHERE dateEpochDay = :epochDay ORDER BY startTimeMinutes ASC, id ASC")
	suspend fun getByDate(epochDay: Long): List<TimeBlockEntity>

	@Query(
		"""
		SELECT * FROM time_blocks
		WHERE dateEpochDay BETWEEN :fromEpochDay AND :toEpochDay
		ORDER BY dateEpochDay ASC, startTimeMinutes ASC, id ASC
		""",
	)
	suspend fun getBetween(fromEpochDay: Long, toEpochDay: Long): List<TimeBlockEntity>

	@Query("SELECT * FROM time_blocks WHERE id = :id LIMIT 1")
	suspend fun getById(id: Long): TimeBlockEntity?

	@Query(
		"""
		SELECT * FROM time_blocks
		WHERE scheduleId = :scheduleId AND dateEpochDay >= :fromEpochDay
		ORDER BY dateEpochDay ASC
		""",
	)
	suspend fun getByScheduleFrom(scheduleId: Long, fromEpochDay: Long): List<TimeBlockEntity>

	@Query(
		"""
		SELECT * FROM time_blocks
		WHERE scheduleId = :scheduleId AND dateEpochDay = :epochDay
		LIMIT 1
		""",
	)
	suspend fun getByScheduleAndDate(scheduleId: Long, epochDay: Long): TimeBlockEntity?

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(entity: TimeBlockEntity): Long

	@Update
	suspend fun update(entity: TimeBlockEntity)

	@Query("DELETE FROM time_blocks WHERE id = :id")
	suspend fun delete(id: Long)

	@Query(
		"""
		DELETE FROM time_blocks
		WHERE scheduleId = :scheduleId AND dateEpochDay >= :fromEpochDay
		""",
	)
	suspend fun deleteByScheduleFrom(scheduleId: Long, fromEpochDay: Long)
}
