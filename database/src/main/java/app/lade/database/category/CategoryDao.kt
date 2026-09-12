package app.lade.database.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
	@Query("SELECT * FROM categories WHERE archivedAtEpochMs IS NULL ORDER BY title ASC")
	fun observeActive(): Flow<List<CategoryEntity>>

	@Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
	suspend fun getById(id: Long): CategoryEntity?

	@Query("SELECT COUNT(*) FROM categories")
	suspend fun count(): Int

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(entity: CategoryEntity): Long

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAll(entities: List<CategoryEntity>)

	@Update
	suspend fun update(entity: CategoryEntity)

	@Query("UPDATE categories SET archivedAtEpochMs = :archivedAt WHERE id = :id")
	suspend fun archive(id: Long, archivedAt: Long)
}
