package app.lade.chatstore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDictDao {
	@Query("SELECT * FROM chat_dicts WHERE archivedAtEpochMs IS NULL ORDER BY kind ASC, title ASC")
	fun observeActive(): Flow<List<ChatDictEntity>>

	@Query("SELECT * FROM chat_dicts WHERE id = :id LIMIT 1")
	suspend fun getById(id: Long): ChatDictEntity?

	@Query("SELECT * FROM chat_dicts WHERE systemKey = :systemKey LIMIT 1")
	suspend fun getBySystemKey(systemKey: String): ChatDictEntity?

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(entity: ChatDictEntity): Long

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAll(entities: List<ChatDictEntity>)

	@Update
	suspend fun update(entity: ChatDictEntity)

	@Query("UPDATE chat_dicts SET archivedAtEpochMs = :archivedAt WHERE id = :id")
	suspend fun archive(id: Long, archivedAt: Long)
}
