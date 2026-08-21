package app.lade.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.lade.database.entity.TimeScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeScheduleDao {
	@Query("SELECT * FROM time_schedules WHERE archivedAtEpochMs IS NULL ORDER BY title ASC")
	fun observeActive(): Flow<List<TimeScheduleEntity>>

	@Query("SELECT * FROM time_schedules WHERE id = :id LIMIT 1")
	suspend fun getById(id: Long): TimeScheduleEntity?

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(entity: TimeScheduleEntity): Long

	@Update
	suspend fun update(entity: TimeScheduleEntity)

	@Query("DELETE FROM time_schedules WHERE id = :id")
	suspend fun delete(id: Long)
}
