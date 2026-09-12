package app.lade.database.entry

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryHistoryDao {
	@Query("SELECT * FROM entry_history WHERE dateEpochDay = :epochDay ORDER BY notedAtEpochMs")
	fun observeByDate(epochDay: Long): Flow<List<EntryHistoryEntity>>

	@Query(
		"""
		SELECT * FROM entry_history
		WHERE dateEpochDay BETWEEN :fromEpoch AND :toEpoch
		ORDER BY dateEpochDay, notedAtEpochMs
		""",
	)
	fun observeBetween(fromEpoch: Long, toEpoch: Long): Flow<List<EntryHistoryEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(entity: EntryHistoryEntity): Long
}
