package app.lade.agendastore.log

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
	@Query("SELECT * FROM logs WHERE entryId = :entryId AND epochDay = :epochDay ORDER BY id")
	suspend fun getByEntryAndDay(entryId: Long, epochDay: Long): List<LogEntity>

	@Query("SELECT * FROM logs WHERE goalId = :goalId AND epochDay = :epochDay ORDER BY id")
	suspend fun getByGoalAndDay(goalId: Long, epochDay: Long): List<LogEntity>

	@Query("SELECT * FROM logs WHERE epochDay = :epochDay ORDER BY createdAtEpochMs")
	fun observeByDay(epochDay: Long): Flow<List<LogEntity>>

	@Query("SELECT * FROM logs WHERE epochDay BETWEEN :fromEpoch AND :toEpoch ORDER BY epochDay, createdAtEpochMs")
	fun observeBetween(fromEpoch: Long, toEpoch: Long): Flow<List<LogEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(entities: List<LogEntity>)

	@Query("DELETE FROM logs WHERE entryId = :entryId AND epochDay = :epochDay")
	suspend fun deleteByEntryAndDay(entryId: Long, epochDay: Long)

	@Query("DELETE FROM logs WHERE goalId IN (:goalIds) AND epochDay = :epochDay")
	suspend fun deleteByGoalIdsAndDay(goalIds: List<Long>, epochDay: Long)

	@Query("DELETE FROM logs WHERE goalId = :goalId AND epochDay = :epochDay")
	suspend fun deleteByGoalAndDay(goalId: Long, epochDay: Long)
}