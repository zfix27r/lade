package app.lade.database.entry

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.lade.database.category.CategoryEntity

@Entity(
	tableName = "entries",
	foreignKeys = [
		ForeignKey(
			entity = CategoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.RESTRICT,
		),
	],
	indices = [
		Index("categoryId"),
		Index("kind"),
	],
)
data class EntryEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val kind: String,
	val title: String,
	val categoryId: Long,
	val dateFromEpochDay: Long? = null,
	val dateToEpochDay: Long? = null,
	val startTimeMinutes: Int? = null,
	val endTimeMinutes: Int? = null,
	val rrule: String? = null,
	val goalDefsJson: String = "[]",
	val alarmMode: String = "none",
	val reminderMinutesBefore: Int? = null,
	val archivedAtEpochMs: Long? = null,
	val pausedAtEpochMs: Long? = null,
	val createdAtEpochMs: Long,
)
