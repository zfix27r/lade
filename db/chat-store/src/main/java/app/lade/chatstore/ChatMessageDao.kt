package app.lade.chatstore

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
	@Query("SELECT * FROM chat_messages ORDER BY createdAtEpochMs ASC, id ASC")
	fun observeAll(): Flow<List<ChatMessageEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(entity: ChatMessageEntity): Long

	@Query("DELETE FROM chat_messages")
	suspend fun clearAll()

	@Query("SELECT COUNT(*) FROM chat_messages")
	suspend fun count(): Int
}
