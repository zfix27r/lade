package app.lade.database.category

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "categories",
	indices = [Index(value = ["key"], unique = true)],
)
data class CategoryEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val key: String? = null,
	val title: String,
	val color: String,
	val archivedAtEpochMs: Long? = null,
)
