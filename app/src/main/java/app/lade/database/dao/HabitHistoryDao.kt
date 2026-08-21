package app.lade.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.lade.database.entity.HabitHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitHistoryDao {
	@Query("SELECT * FROM habit_history WHERE dateEpochDay = :epochDay ORDER BY id ASC")
	fun observeByDate(epochDay: Long): Flow<List<HabitHistoryEntity>>

	@Query(
		"""
		SELECT * FROM habit_history
		WHERE dateEpochDay BETWEEN :fromEpochDay AND :toEpochDay
		ORDER BY dateEpochDay DESC, id DESC
		""",
	)
	fun observeBetween(fromEpochDay: Long, toEpochDay: Long): Flow<List<HabitHistoryEntity>>

	@Query(
		"""
		SELECT * FROM habit_history
		WHERE habitId = :habitId AND dateEpochDay BETWEEN :fromEpochDay AND :toEpochDay
		ORDER BY dateEpochDay ASC
		""",
	)
	fun observeForHabitBetween(
		habitId: Long,
		fromEpochDay: Long,
		toEpochDay: Long,
	): Flow<List<HabitHistoryEntity>>

	@Query("SELECT * FROM habit_history WHERE habitId = :habitId AND dateEpochDay = :epochDay LIMIT 1")
	suspend fun get(habitId: Long, epochDay: Long): HabitHistoryEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsert(entity: HabitHistoryEntity): Long

	@Update
	suspend fun update(entity: HabitHistoryEntity)

	@Query("DELETE FROM habit_history WHERE id = :id")
	suspend fun delete(id: Long)
}
