package app.lade.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.lade.database.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
	@Query("SELECT * FROM habits WHERE archivedAtEpochMs IS NULL ORDER BY title ASC")
	fun observeActive(): Flow<List<HabitEntity>>

	@Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
	suspend fun getById(id: Long): HabitEntity?

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(entity: HabitEntity): Long

	@Update
	suspend fun update(entity: HabitEntity)

	@Query("DELETE FROM habits WHERE id = :id")
	suspend fun delete(id: Long)
}
