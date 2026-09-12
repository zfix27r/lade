package app.lade.database.entry

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "entry_history",
	foreignKeys = [
		ForeignKey(
			entity = EntryEntity::class,
			parentColumns = ["id"],
			childColumns = ["entryId"],
			onDelete = ForeignKey.SET_NULL,
		),
	],
	indices = [
		Index("entryId"),
		Index("dateEpochDay"),
		Index(value = ["entryId", "dateEpochDay"]),
	],
)
data class EntryHistoryEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val entryId: Long? = null,
	val kind: String,
	val title: String,
	val categoryId: Long,
	val dateEpochDay: Long,
	val startTimeMinutes: Int? = null,
	val endTimeMinutes: Int? = null,
	val result: String? = null,
	val goalsJson: String = "[]",
	val actualsJson: String = "[]",
	val notedAtEpochMs: Long,
	val source: String? = null,
)
