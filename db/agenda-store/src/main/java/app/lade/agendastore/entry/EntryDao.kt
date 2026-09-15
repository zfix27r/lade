package app.lade.agendastore.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
	@Query("SELECT * FROM entries WHERE archivedAtEpochMs IS NULL ORDER BY title COLLATE NOCASE")
	fun observeActive(): Flow<List<EntryEntity>>

	@Query("SELECT * FROM entries ORDER BY title COLLATE NOCASE")
	fun observeAll(): Flow<List<EntryEntity>>

	@Query("SELECT * FROM entries WHERE archivedAtEpochMs IS NOT NULL ORDER BY title COLLATE NOCASE")
	fun observeArchived(): Flow<List<EntryEntity>>

	@Query("SELECT * FROM entries WHERE id = :id LIMIT 1")
	suspend fun getById(id: Long): EntryEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(entity: EntryEntity): Long

	@Update
	suspend fun update(entity: EntryEntity)

	@Query("SELECT COUNT(*) FROM entries")
	suspend fun count(): Int
}
