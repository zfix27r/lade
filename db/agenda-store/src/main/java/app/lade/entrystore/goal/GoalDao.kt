package app.lade.entrystore.goal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE entryId = :entryId ORDER BY id")
    suspend fun getByEntryId(entryId: Long): List<GoalEntity>

    @Query("SELECT * FROM goals WHERE entryId = :entryId ORDER BY id")
    fun observeByEntryId(entryId: Long): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals ORDER BY entryId, id")
    suspend fun getAll(): List<GoalEntity>

    @Query("SELECT * FROM goals ORDER BY entryId, id")
    fun observeAll(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): GoalEntity?

    @Query("SELECT * FROM goals WHERE entryId IN (:entryIds) ORDER BY entryId, id")
    suspend fun getByEntryIds(entryIds: List<Long>): List<GoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GoalEntity>)

    @Update
    suspend fun update(entity: GoalEntity)

    @Query("DELETE FROM goals WHERE entryId = :entryId")
    suspend fun deleteByEntryId(entryId: Long)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun count(): Int
}